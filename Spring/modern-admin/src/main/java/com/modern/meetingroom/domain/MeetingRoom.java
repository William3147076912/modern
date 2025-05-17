package com.modern.meetingroom.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.modern.common.annotation.Excel;
import com.modern.common.core.domain.BaseEntity;

/**
 * 会议室基础信息，记录公司所有会议室的配置及状态对象 meeting_room
 *
 * @author cyk
 * @date 2025-04-03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MeetingRoom extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 会议室主键ID，系统自动生成（自增）
     */
    private Long roomId;

    /**
     * 会议室名称（如"大会议室301"），需保证唯一性
     */
    @Excel(name = "会议室名称", readConverterExp = "如=大会议室301")
    private String roomName;

    /**
     * 会议室类型：教室型（带主席台）、圆桌型（大型会议桌）、普通会议室（默认值）
     */
    @Excel(name = "会议室类型：教室型", readConverterExp = "带=主席台")
    private String roomType;

    /**
     * 最大座位数（含主席台座位），取值范围1-500
     */
    @Excel(name = "最大座位数", readConverterExp = "含=主席台座位")
    private Long seatingCapacity;

    /**
     * 多媒体设备列表（如"投影+音响+网络"），逗号分隔，空值表示无设备
     */
    @Excel(name = "多媒体设备列表", readConverterExp = "如=投影+音响+网络")
    private String equipment;

    /**
     * 每小时租赁价格（单位：元）
     */
    @Excel(name = "每小时租赁价格", readConverterExp = "单位：元")
    private Double price;

    /**
     * 状态说明：
     * vacancy=空闲（可预定）,
     * locked=锁定（预定中未支付）,
     * reserved=预定成功（已支付）,
     * busy=使用中（占用中）,
     * maintaining=维护中（暂停使用）
     */
    @Excel(name = "状态说明：vacancy=空闲", readConverterExp = "可=预定")
    private String roomStatus;

}
