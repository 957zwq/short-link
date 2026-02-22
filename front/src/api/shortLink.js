import request from '@/utils/request'

// 创建短链接
export const createShortLink = (data) => {
  return request({
    url: '/short-link/admin/v1/create',
    method: 'post',
    data
  })
}

// 分页查询短链接
export const pageShortLink = (params) => {
  return request({
    url: '/short-link/admin/v1/page',
    method: 'get',
    params
  })
}
