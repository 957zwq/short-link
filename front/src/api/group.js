import request from '@/utils/request'

// 查询分组集合
export const getGroups = () => {
  return request({
    url: '/short-link/admin/v1/group',
    method: 'get'
  })
}

// 新增分组
export const createGroup = (data) => {
  return request({
    url: '/short-link/admin/v1/group',
    method: 'post',
    data
  })
}

// 修改分组
export const updateGroup = (data) => {
  return request({
    url: '/short-link/admin/v1/group',
    method: 'put',
    data
  })
}

// 删除分组
export const deleteGroup = (gid) => {
  return request({
    url: '/short-link/admin/v1/group',
    method: 'delete',
    params: { gid }
  })
}

// 排序分组
export const sortGroup = (data) => {
  return request({
    url: '/short-link/admin/v1/group/sort',
    method: 'post',
    data
  })
}
