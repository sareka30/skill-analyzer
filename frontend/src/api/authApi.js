import axiosClient from './axiosClient'

export const login = (username, password) =>
  axiosClient.post('/auth/login', { username, password }).then((res) => res.data)

export const register = (username, email, password) =>
  axiosClient.post('/auth/register', { username, email, password }).then((res) => res.data)

export const getProfile = () => axiosClient.get('/student/me').then((res) => res.data)

export const updateMySkills = (skillIds) =>
  axiosClient.put('/student/skills', { skillIds }).then((res) => res.data)
