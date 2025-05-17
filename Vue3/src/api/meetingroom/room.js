import request from '@/utils/request'

// 查询会议室基础信息，记录公司所有会议室的配置及状态列表
export function listRoom(query) {
  return request({
    url: '/meetingroom/room/list',
    method: 'get',
    params: query
  })
}
// 查询空闲的会议室基础信息
export function listIdleRoom(query) {
  return request({
    url: '/meetingroom/room/idleRoomList',
    method: 'get',
    params: query
  })
}

// 查询会议室基础信息，记录公司所有会议室的配置及状态详细
export function getRoom(roomId) {
  return request({
    url: '/meetingroom/room/' + roomId,
    method: 'get'
  })
}

// 新增会议室基础信息，记录公司所有会议室的配置及状态
export function addRoom(data) {
  return request({
    url: '/meetingroom/room',
    method: 'post',
    data: data
  })
}

// 修改会议室基础信息，记录公司所有会议室的配置及状态
export function updateRoom(data) {
  return request({
    url: '/meetingroom/room',
    method: 'put',
    data: data
  })
}

// 删除会议室基础信息，记录公司所有会议室的配置及状态
export function delRoom(roomId) {
  return request({
    url: '/meetingroom/room/' + roomId,
    method: 'delete'
  })
}
