package com.modern.order.domain.dto;

import lombok.Data;

/**
 * 这个类是：
 *
 * @author: William
 * @date: 2025-05-10T18:16:46CST 18:16
 * @description:
 */
@Data
public class RefundRequestDTO {
    private Long orderId;
    private String refundReason;
}
