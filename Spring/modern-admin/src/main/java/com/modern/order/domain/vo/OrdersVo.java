package com.modern.order.domain.vo;

import com.modern.order.domain.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 这个类是：
 *
 * @author: William
 * @date: 2025-05-07T16:41:00CST 16:41
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class OrdersVo extends Orders {
    private String roomName;
    private String userName;
}
