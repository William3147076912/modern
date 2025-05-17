package com.modern.order.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.modern.common.annotation.Excel;
import com.modern.common.core.domain.BaseEntity;

/**
 * 会议室订单对象 orders
 *
 * @author cyk
 * @date 2025-04-03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Orders extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 订单主键（自增）
     */
    private Long orderId;

    /**
     * 关联会议室ID（外键）
     */
    @Excel(name = "关联会议室ID", readConverterExp = "外=键")
    private Long roomId;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 订单金额（元/小时）
     */
    @Excel(name = "订单金额", readConverterExp = "元=/小时")
    private Double price;

    /**
     * 订单状态
     */
    @Excel(name = "订单状态")
    private String orderStatus;
    /**
     * 预约时间
     */
    @Excel(name = "使用时间")
    private String usageTime;
    /**
     * 退款原因
     */
    @Excel(name = "退款原因")
    private String refundReason;
    /**
     * 退款金额
     */
    @Excel(name = "退款金额")
    private Double refundPrice;

}
