import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    if (token) {
      config.headers['token'] = token
    }
    if (username) {
      config.headers['username'] = username
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    // 成功返回码是 "0"
    if (res.code === '0') {
      return res
    }
    // 错误处理
    const errorMsg = res.message || '请求失败'
    // 登录相关错误，清除登录信息
    if (res.code === 'A000001' || res.code === 'A000130' || res.code === 'A000131' || res.code === 'A000132') {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      window.location.href = '/login'
    }
    return Promise.reject(new Error(errorMsg))
  },
  error => {
    console.error('请求错误:', error)
    // 网络错误或服务器未响应
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status
      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default request
