import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const axiosClient = axios.create({ baseURL })

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('sga_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('sga_token')
      localStorage.removeItem('sga_user')
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default axiosClient
