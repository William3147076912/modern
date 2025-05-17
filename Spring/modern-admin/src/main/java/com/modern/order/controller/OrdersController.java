package com.modern.order.controller;

import com.modern.common.annotation.Log;
import com.modern.common.constant.HttpStatus;
import com.modern.common.core.controller.BaseController;
import com.modern.common.core.domain.AjaxResult;
import com.modern.common.core.domain.entity.SysUser;
import com.modern.common.core.page.PageDomain;
import com.modern.common.core.page.TableDataInfo;
import com.modern.common.core.page.TableSupport;
import com.modern.common.enums.BusinessType;
import com.modern.common.utils.poi.ExcelUtil;
import com.modern.order.domain.Orders;
import com.modern.order.domain.dto.RefundRequestDTO;
import com.modern.order.domain.vo.OrdersVo;
import com.modern.order.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议室订单Controller
 *
 * @author cyk
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/order/orders")
public class OrdersController extends BaseController {
    @Autowired
    private IOrdersService ordersService;

    /**
     * 查询会议室订单列表
     */
    @PreAuthorize("@ss.hasPermi('order:orders:list')")
    @GetMapping("/list")
    public TableDataInfo list(OrdersVo orders) {
        startPage();
        List<OrdersVo> list = ordersService.selectOrdersList(orders);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('order:orders:list')")
    @GetMapping("/userList")
    public TableDataInfo userList() {
        startPage();
        List<SysUser> list = ordersService.selectUserList();
        return getDataTable(list);
    }

    /**
     * 导出会议室订单列表
     */
    @PreAuthorize("@ss.hasPermi('order:orders:export')")
    @Log(title = "会议室订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Orders orders) {
        List<OrdersVo> list = ordersService.selectOrdersList(orders);
        ExcelUtil<OrdersVo> util = new ExcelUtil<>(OrdersVo.class);
        util.exportExcel(response, list, "会议室订单数据");
    }

    /**
     * 获取会议室订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('order:orders:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId) {
        return success(ordersService.selectOrdersByOrderId(orderId));
    }

    /**
     * 新增会议室订单
     */
    @PreAuthorize("@ss.hasAnyPermi('order:orders:add,room:room:reserve')")
    @Log(title = "会议室订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Orders orders) {
        return toAjax(ordersService.insertOrders(orders));
    }

    /**
     * 修改会议室订单
     */
    @PreAuthorize("@ss.hasAnyPermi('order:orders:edit,order:orders:pay_unsubscribe,order:orders:examine')")
    @Log(title = "会议室订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Orders orders) {
        return toAjax(ordersService.updateOrders(orders));
    }

    /**
     * 支付会议室订单
     */
    @PreAuthorize("@ss.hasPermi('order:orders:pay_unsubscribe')")
    @Log(title = "会议室订单", businessType = BusinessType.UPDATE)
    @PutMapping("/pay/{orderId}")
    public AjaxResult pay(@PathVariable("orderId") Long orderId) {
        return toAjax(ordersService.payOrder(orderId));
    }

    /**
     * 获取退款金额
     */
    @PreAuthorize("@ss.hasPermi('order:orders:pay_unsubscribe')")
    @Log(title = "会议室订单", businessType = BusinessType.UPDATE)
    @PutMapping("/getRefund/{orderId}")
    public Double getRefund(@PathVariable("orderId") Long orderId) {
        return ordersService.getRefund(orderId);
    }

    /**
     * 提交退订会议室订单
     */
    @PreAuthorize("@ss.hasPermi('order:orders:pay_unsubscribe')")
    @Log(title = "会议室订单", businessType = BusinessType.UPDATE)
    @PostMapping("/postUnsubscribe")
    public AjaxResult postUnsubscribe(@RequestBody RefundRequestDTO dto) {
        return success(ordersService.postUnsubscribe(dto.getOrderId(), dto.getRefundReason()));
    }

    /**
     * 删除会议室订单
     */
    @PreAuthorize("@ss.hasPermi('order:orders:remove')")
    @Log(title = "会议室订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds) {
        return toAjax(ordersService.deleteOrdersByOrderIds(orderIds));
    }
}
