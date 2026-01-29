import request from '@/utils/request'

// 用户注册
export const register = (data) => {
  return request({
    url: '/shortlink/v1/user/register',
    method: 'post',
    data
  })
}

// 用户登录
export const login = (data) => {
  return request({
    url: '/short-link/admin/v1/user/login',
    method: 'post',
    data
  })
}

// 检查登录状态
export const checkLogin = () => {
  return request({
    url: '/short-link/admin/v1/user/check-login',
    method: 'get'
  })
}

// 用户登出
export const logout = () => {
  return request({
    url: '/short-link/admin/v1/user/logout',
    method: 'delete'
  })
}

// 查询用户
export const getUserByUsername = (username) => {
  return request({
    url: `/shortlink/v1/user/${username}`,
    method: 'get'
  })
}

// 修改用户
export const updateUser = (data) => {
  return request({
    url: '/shortlink/v1/user',
    method: 'put',
    data
  })
}

// 检查用户名是否存在
export const checkUsername = (username) => {
  return request({
    url: `/shortlink/v1/user/username/${username}/exists`,
    method: 'get'
  })
}
