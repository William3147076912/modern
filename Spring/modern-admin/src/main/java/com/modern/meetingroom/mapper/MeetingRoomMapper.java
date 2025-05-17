package com.modern.meetingroom.mapper;

import java.util.List;
import com.modern.meetingroom.domain.MeetingRoom;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会议室基础信息，记录公司所有会议室的配置及状态Mapper接口
 * 
 * @author cyk
 * @date 2025-04-03
 */
@Mapper
public interface MeetingRoomMapper 
{
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
     * 删除会议室基础信息，记录公司所有会议室的配置及状态
     * 
     * @param roomId 会议室基础信息，记录公司所有会议室的配置及状态主键
     * @return 结果
     */
    public int deleteMeetingRoomByRoomId(Long roomId);

    /**
     * 批量删除会议室基础信息，记录公司所有会议室的配置及状态
     * 
     * @param roomIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMeetingRoomByRoomIds(Long[] roomIds);
}
