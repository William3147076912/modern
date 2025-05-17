package com.modern.order.service.impl;

import com.modern.common.core.domain.entity.SysUser;
import com.modern.common.core.redis.RedisCache;
import com.modern.common.utils.DateUtils;
import com.modern.common.utils.TimeRange;
import com.modern.meetingroom.mapper.MeetingRoomMapper;
import com.modern.order.domain.Orders;
import com.modern.order.domain.vo.OrdersVo;
import com.modern.order.mapper.OrdersMapper;
import com.modern.order.service.IOrdersService;
import com.modern.system.mapper.SysUserMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrdersServiceImpl implements IOrdersService {
    private static final String ORDERS_CACHE_KEY = "ordersCache:";
    private static final String ORDER_LOCK_KEY = "orderLock:";
    private static final String MESSAGE_ID_KEY = "messageId:";

    // 令牌桶配置
    private final long capacity = 100; // 桶的容量
    private final long refillTokens = 10; // 每次补充的令牌数
    private final long refillPeriodMillis = 1000; // 补充周期(毫秒)
    private final Lock bucketLock = new ReentrantLock(); // 用于令牌桶操作的锁
    private AtomicLong tokens; // 当前令牌数
    private long lastRefillTime; // 上次补充时间
    @Resource
    private MeetingRoomMapper roomMapper;
    @Value("${order.overtime}")
    private int overtime = 30;
    @Autowired
    private OrdersMapper ordersMapper;
    @Resource
    private SysUserMapper userMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private RedissonClient redissonClient;

    public OrdersServiceImpl() {
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTime = System.currentTimeMillis();
    }

    // 创建定时器
    private void createTimer(Orders orders, String formattedTime) {
        String eventName = "timer_" + orders.getOrderId();
        String sqlStatement = String.format(
                "BEGIN " +
                        "UPDATE orders SET order_status = '已完成' WHERE order_id = %d;" +
                        "UPDATE meeting_room SET room_status = 'vacancy' WHERE room_id = %d;" +
                        "END",
                orders.getOrderId(), orders.getRoomId()
        );
        String createEventSql = String.format(
                "CREATE EVENT IF NOT EXISTS %s " +
                        "ON SCHEDULE AT '%s' " +
                        "DO %s",
                eventName, formattedTime, sqlStatement
        );
        jdbcTemplate.execute(createEventSql);
    }

    // 关闭定时器
    private void deleteTimer(Long orderId) {
        String eventName = "timer_" + orderId;
        String dropEventSql = String.format("DROP EVENT IF EXISTS %s", eventName);
        jdbcTemplate.execute(dropEventSql);
    }

    @Override
    public Orders selectOrdersByOrderId(Long orderId) {
        String cacheKey = ORDERS_CACHE_KEY + orderId;
        Orders orders = redisCache.getCacheObject(cacheKey);

        if (orders == null) {
            orders = ordersMapper.selectOrdersByOrderId(orderId);
            if (orders != null) {
                redisCache.setCacheObject(cacheKey, orders, 60, TimeUnit.MINUTES);
            }
        }
        return orders;
    }

    @Override
    public List<OrdersVo> selectOrdersList(Orders orders) {
        List<OrdersVo> list = ordersMapper.selectOrdersList(orders);
        return list;
    }

    @Override
    public List<SysUser> selectUserList() {
        return userMapper.selectUserList(new SysUser());
    }

    @Override
    public int insertOrders(Orders orders) {
        // 令牌桶限流
        if (!tryAcquireToken()) {
            throw new RuntimeException("系统繁忙，请稍后再试");
        }

        // 生成唯一的消息ID
        String messageId = UUID.randomUUID().toString();
        String messageKey = MESSAGE_ID_KEY + messageId;

        // 检查是否已经处理过该消息
        if (redisCache.hasKey(messageKey)) {
            throw new RuntimeException("Duplicate message detected");
        }

        // 设置消息ID到Redis，防止重复提交
        redisCache.setCacheObject(messageKey, messageId, 1, TimeUnit.MINUTES);

        // 使用分布式锁确保订单生成的唯一性
        String lockKey = ORDER_LOCK_KEY + orders.getRoomId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();
            // 检查订单是否已经存在
            Orders existingOrder = ordersMapper.selectOrdersByOrderId(orders.getOrderId());
            if (existingOrder != null) {
                throw new RuntimeException("Order already exists");
            }

            // 插入订单
            orders.setCreateTime(DateUtils.getNowDate());
            int result = ordersMapper.insertOrders(orders);

            // 用于订单超时判定
            LocalDateTime executeTime = LocalDateTime.now().plusMinutes(overtime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = executeTime.format(formatter);
            createTimer(orders, formattedTime);
            return result;
        } finally {
            lock.unlock();
        }
    }

    // 令牌桶限流核心方法
    private boolean tryAcquireToken() {
        bucketLock.lock();
        try {
            // 补充令牌
            refillTokens();

            // 尝试获取令牌
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        } finally {
            bucketLock.unlock();
        }
    }

    // 补充令牌
    private void refillTokens() {
        long now = System.currentTimeMillis();
        if (now > lastRefillTime) {
            long elapsedTime = now - lastRefillTime;
            long numRefills = elapsedTime / refillPeriodMillis;

            if (numRefills > 0) {
                long newTokens = Math.min(capacity, tokens.get() + numRefills * refillTokens);
                tokens.set(newTokens);
                lastRefillTime = now;
            }
        }
    }

    @Override
    public int updateOrders(Orders orders) {
        int result = ordersMapper.updateOrders(orders);
        if (result > 0) {
            String cacheKey = ORDERS_CACHE_KEY + orders.getOrderId();
            redisCache.setCacheObject(cacheKey, orders, 60, TimeUnit.MINUTES);
        }
        return result;
    }

    @Override
    public int payOrder(Long orderId) {
        int result = ordersMapper.payOrder(orderId);
        if (result > 0) {
            String cacheKey = ORDERS_CACHE_KEY + orderId;
            Orders orders = ordersMapper.selectOrdersByOrderId(orderId);
            redisCache.setCacheObject(cacheKey, orders, 60, TimeUnit.MINUTES);
            // 支付成功后将定时器关闭
            deleteTimer(orderId);
        }
        return result;
    }

    @Override
    public Double getRefund(Long orderId) {
        Orders orders = ordersMapper.selectOrdersByOrderId(orderId);
        return TimeRange.getRefundAmount(orders.getUsageTime(), orders.getPrice());
    }

    @Override
    public String postUnsubscribe(Long orderId, String refundReason) {
        Orders orders = ordersMapper.selectOrdersByOrderId(orderId);
        orders.setRefundReason(refundReason);
        orders.setRefundPrice(TimeRange.getRefundAmount(orders.getUsageTime(), orders.getPrice()));
        orders.setOrderStatus("待审核");
        int result = ordersMapper.updateOrders(orders);
        if (result > 0) {
            String cacheKey = ORDERS_CACHE_KEY + orderId;
            redisCache.setCacheObject(cacheKey, orders, 60, TimeUnit.MINUTES);
        }
        return "提交成功";
    }

    @Override
    public int deleteOrdersByOrderIds(Long[] orderIds) {
        int result = ordersMapper.deleteOrdersByOrderIds(orderIds);
        if (result > 0) {
            for (Long orderId : orderIds) {
                String cacheKey = ORDERS_CACHE_KEY + orderId;
                redisCache.deleteObject(cacheKey);
            }
        }
        return result;
    }

    @Override
    public int deleteOrdersByOrderId(Long orderId) {
        int result = ordersMapper.deleteOrdersByOrderId(orderId);
        if (result > 0) {
            String cacheKey = ORDERS_CACHE_KEY + orderId;
            redisCache.deleteObject(cacheKey);
        }
        return result;
    }
}