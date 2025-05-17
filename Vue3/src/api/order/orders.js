import request from '@/utils/request'

// 查询会议室订单列表
export function listOrders(query) {
    return request({
        url: '/order/orders/list',
        method: 'get',
        params: query
    })
}

export function listUser() {
    return request({
        url: '/order/orders/userList',
        method: 'get',
    })
}


export function listOrdersById(query) {
    return request({
        url: '/order/orders/listById',
        method: 'get',
        params: query
    })
}

// 查询会议室订单详细
export function getOrders(orderId) {
    return request({
        url: '/order/orders/' + orderId,
        method: 'get'
    })
}

// 新增会议室订单
export function addOrders(data) {
    return request({
        url: '/order/orders',
        method: 'post',
        data: data
    })
}

// 修改会议室订单
export function updateOrders(data) {
    return request({
        url: '/order/orders',
        method: 'put',
        data: data
    })
}
// 支付会议室订单
export function payOrder(orderId) {
    return request({
        url: '/order/orders/pay/' + orderId,
        method: 'put',
    })
}
// 获取退款金额
export function getRefund(orderId) {
    return request({
        url: '/order/orders/getRefund/' + orderId,
        method: 'put',
    })
}
// 提交退款订单
export function postUnsubscribe(data) {
    return request({
        url: '/order/orders/postUnsubscribe/',
        method: 'post',
        data: data
    })
}

// 删除会议室订单
export function delOrders(orderId) {
    return request({
        url: '/order/orders/' + orderId,
        method: 'delete'
    })
}
