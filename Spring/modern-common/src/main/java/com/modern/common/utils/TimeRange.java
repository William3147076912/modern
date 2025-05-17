package com.modern.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 这个类是：
 *
 * @author: William
 * @date: 2025-05-09T15:34:45CST 15:34
 * @description:
 */

public class TimeRange {
    private LocalDateTime start;
    private LocalDateTime end;

    public TimeRange(String rangeStr) {
        // 定义格式化模板
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");
        String[] parts = rangeStr.split("_");
        // 拼接日期和时间部分
        String startDateStr = parts[0] + "_" + parts[1];
        String endDateStr = parts[0] + "_" + parts[2];

        // 解析开始和结束时间
        this.start = LocalDateTime.parse(startDateStr, formatter);
        this.end = LocalDateTime.parse(endDateStr, formatter);
    }

    // 根据当前时间与传入时间返回退款金额
    /*
     * 需要提前 24 小时申请取消预约
     * 提前 72 小时退全款；提前 48 小时退 75%；提前 24 小时退 25%
     */
    public static Double getRefundAmount(String orderTime, Double price) {
        LocalDateTime time = new TimeRange(orderTime).start;
        if (LocalDateTime.now().isBefore(time.minusHours(72))) {
            return price;
        } else if (LocalDateTime.now().isBefore(time.minusHours(48))) {
            return price * 0.75;
        } else if (LocalDateTime.now().isBefore(time.plusHours(24))) {
            return price * 0.25;
        } else {
            // -1代表不能退款
            return -1.0;
        }
    }

    public static boolean isOverlapping(String range1, String range2) {
        TimeRange timeRange1 = new TimeRange(range1);
        TimeRange timeRange2 = new TimeRange(range2);
        return timeRange1.isOverlapping(timeRange2);
    }

    public LocalDateTime getStart() {
        return start;
    }


    public LocalDateTime getEnd() {
        return end;
    }

    public boolean isOverlapping(TimeRange other) {
        // 当前区间的开始或结束时间在另一个区间内，则存在交叉
        return this.start.isBefore(other.getEnd()) && this.end.isAfter(other.getStart());
    }
}
