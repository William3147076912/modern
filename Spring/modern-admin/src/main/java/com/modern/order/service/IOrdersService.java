package com.modern.order.service;

import java.util.List;

import com.modern.common.core.domain.entity.SysUser;
import com.modern.order.domain.Orders;
import com.modern.order.domain.vo.OrdersVo;

/**
 * 会议室订单Service接口
 *
 * @author cyk
 * @date 2025-04-03
 */
public interface IOrdersService {
    /**
     * 查询会议室订单
     *
     * @param orderId 会议室订单主键
     * @return 会议室订单
     */
    public Orders selectOrdersByOrderId(Long orderId);

    /**
     * 查询会议室订单列表
     *
     * @param orders 会议室订单
     * @return 会议室订单集合
     */
    public List<OrdersVo> selectOrdersList(Orders orders);

    public List<SysUser> selectUserList();

    /**
     * 新增会议室订单
     *
     * @param orders 会议室订单
     * @return 结果
     */
    public int insertOrders(Orders orders);

    /**
     * 修改会议室订单
     *
     * @param orders 会议室订单
     * @return 结果
     */
    public int updateOrders(Orders orders);

    /**
     * 批量删除会议室订单
     *
     * @param orderIds 需要删除的会议室订单主键集合
     * @return 结果
     */
    public int deleteOrdersByOrderIds(Long[] orderIds);

    /**
     * 删除会议室订单信息
     *
     * @param orderId 会议室订单主键
     * @return 结果
     */
    public int deleteOrdersByOrderId(Long orderId);

    int payOrder(Long orderId);

    Double getRefund(Long orderId);

    String postUnsubscribe(Long orderId, String refundReason);
}
