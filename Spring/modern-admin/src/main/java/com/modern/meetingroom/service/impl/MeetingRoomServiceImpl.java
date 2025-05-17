package com.modern.meetingroom.service.impl;

import com.modern.common.core.redis.RedisCache;
import com.modern.common.utils.TimeRange;
import com.modern.meetingroom.domain.MeetingRoom;
import com.modern.meetingroom.mapper.MeetingRoomMapper;
import com.modern.meetingroom.service.IMeetingRoomService;
import com.modern.order.domain.Orders;
import com.modern.order.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 会议室基础信息，记录公司所有会议室的配置及状态Service业务层处理
 *
 * @author cyk
 * @date 2025-04-03
 */
@Slf4j
@Service
public class MeetingRoomServiceImpl implements IMeetingRoomService {
    @Autowired
    private MeetingRoomMapper meetingRoomMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Autowired
    private RedisCache redisCache;

    private static final String MEETING_ROOM_CACHE_KEY = "meetingRoomCache:";

    /**
     * 查询会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param roomId 会议室基础信息，记录公司所有会议室的配置及状态主键
     * @return 会议室基础信息，记录公司所有会议室的配置及状态
     */
    @Override
    public MeetingRoom selectMeetingRoomByRoomId(Long roomId) {
        // 尝试从缓存中获取会议室详情
        String cacheKey = MEETING_ROOM_CACHE_KEY + roomId;
        MeetingRoom meetingRoom = redisCache.getCacheObject(cacheKey);

        if (meetingRoom == null) {
            // 如果缓存中没有，从数据库中查询并存入缓存
            meetingRoom = meetingRoomMapper.selectMeetingRoomByRoomId(roomId);
            if (meetingRoom != null) {
                redisCache.setCacheObject(cacheKey, meetingRoom, 60, TimeUnit.MINUTES);
            }
        }
        return meetingRoom;
    }

    /**
     * 查询会议室基础信息，记录公司所有会议室的配置及状态列表
     *
     * @param meetingRoom 会议室基础信息，记录公司所有会议室的配置及状态
     * @return 会议室基础信息，记录公司所有会议室的配置及状态
     */
    @Override
    public List<MeetingRoom> selectMeetingRoomList(MeetingRoom meetingRoom) {
        return meetingRoomMapper.selectMeetingRoomList(meetingRoom);
    }

    /**
     * 新增会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param meetingRoom 会议室基础信息，记录公司所有会议室的配置及状态
     * @return 结果
     */
    @Override
    public int insertMeetingRoom(MeetingRoom meetingRoom) {
        int result = meetingRoomMapper.insertMeetingRoom(meetingRoom);
        if (result > 0) {
            // 新增会议室后，更新缓存
            String cacheKey = MEETING_ROOM_CACHE_KEY + meetingRoom.getRoomId();
            redisCache.setCacheObject(cacheKey, meetingRoom, 60, TimeUnit.MINUTES);
        }
        return result;
    }

    /**
     * 修改会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param meetingRoom 会议室基础信息，记录公司所有会议室的配置及状态
     * @return 结果
     */
    @Override
    public int updateMeetingRoom(MeetingRoom meetingRoom) {
        int result = meetingRoomMapper.updateMeetingRoom(meetingRoom);
        if (result > 0) {
            // 更新会议室后，更新缓存
            String cacheKey = MEETING_ROOM_CACHE_KEY + meetingRoom.getRoomId();
            redisCache.setCacheObject(cacheKey, meetingRoom, 60, TimeUnit.MINUTES);
        }
        return result;
    }

    /**
     * 批量删除会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param roomIds 需要删除的会议室基础信息，记录公司所有会议室的配置及状态主键
     * @return 结果
     */
    @Override
    public int deleteMeetingRoomByRoomIds(Long[] roomIds) {
        int result = meetingRoomMapper.deleteMeetingRoomByRoomIds(roomIds);
        if (result > 0) {
            // 删除会议室后，删除缓存
            for (Long roomId : roomIds) {
                String cacheKey = MEETING_ROOM_CACHE_KEY + roomId;
                redisCache.deleteObject(cacheKey);
            }
        }
        return result;
    }

    /**
     * 删除会议室基础信息，记录公司所有会议室的配置及状态信息
     *
     * @param roomId 会议室基础信息，记录公司所有会议室的配置及状态主键
     * @return 结果
     */
    @Override
    public int deleteMeetingRoomByRoomId(Long roomId) {
        int result = meetingRoomMapper.deleteMeetingRoomByRoomId(roomId);
        if (result > 0) {
            // 删除会议室后，删除缓存
            String cacheKey = MEETING_ROOM_CACHE_KEY + roomId;
            redisCache.deleteObject(cacheKey);
        }
        return result;
    }

    @Override
    public List<MeetingRoom> selectIdleRoomList(MeetingRoom meetingRoom, String usageTime) {
        meetingRoom.setRoomStatus("vacancy");
        return meetingRoomMapper.selectMeetingRoomList(meetingRoom);
    }
}
