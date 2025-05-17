package com.modern.meetingroom.service;

import com.modern.meetingroom.domain.MeetingRoom;

import java.util.List;

/**
 * 会议室基础信息，记录公司所有会议室的配置及状态Service接口
 *
 * @author cyk
 * @date 2025-04-03
 */
public interface IMeetingRoomService {
    /**
     * 查询会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param roomId 会议室基础信息，记录公司所有会议室的配置及状态主键
     * @return 会议室基础信息，记录公司所有会议室的配置及状态
     */
    public MeetingRoom selectMeetingRoomByRoomId(Long roomId);

    /**
     * 查询会议室基础信息，记录公司所有会议室的配置及状态列表
     *
     * @param meetingRoom 会议室基础信息，记录公司所有会议室的配置及状态
     * @return 会议室基础信息，记录公司所有会议室的配置及状态集合
     */
    public List<MeetingRoom> selectMeetingRoomList(MeetingRoom meetingRoom);

    /**
     * 新增会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param meetingRoom 会议室基础信息，记录公司所有会议室的配置及状态
     * @return 结果
     */
    public int insertMeetingRoom(MeetingRoom meetingRoom);

    /**
     * 修改会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param meetingRoom 会议室基础信息，记录公司所有会议室的配置及状态
     * @return 结果
     */
    public int updateMeetingRoom(MeetingRoom meetingRoom);

    /**
     * 批量删除会议室基础信息，记录公司所有会议室的配置及状态
     *
     * @param roomIds 需要删除的会议室基础信息，记录公司所有会议室的配置及状态主键集合
     * @return 结果
     */
    public int deleteMeetingRoomByRoomIds(Long[] roomIds);

    /**
     * 删除会议室基础信息，记录公司所有会议室的配置及状态信息
     *
     * @param roomId 会议室基础信息，记录公司所有会议室的配置及状态主键
     * @return 结果
     */
    public int deleteMeetingRoomByRoomId(Long roomId);

    List<MeetingRoom> selectIdleRoomList(MeetingRoom meetingRoom, String reserveTime);
}
