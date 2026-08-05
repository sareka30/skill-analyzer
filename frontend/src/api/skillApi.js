import axiosClient from './axiosClient'

export const getSkills = () => axiosClient.get('/skills').then((res) => res.data)

export const createSkill = (skill) => axiosClient.post('/admin/skills', skill).then((res) => res.data)

export const updateSkill = (id, skill) =>
  axiosClient.put(`/admin/skills/${id}`, skill).then((res) => res.data)

export const deleteSkill = (id) => axiosClient.delete(`/admin/skills/${id}`)
