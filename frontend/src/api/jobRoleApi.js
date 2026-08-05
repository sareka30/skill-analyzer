import axiosClient from './axiosClient'

export const getJobRoles = () => axiosClient.get('/roles').then((res) => res.data)

export const getJobRole = (id) => axiosClient.get(`/roles/${id}`).then((res) => res.data)

export const createJobRole = (role) => axiosClient.post('/admin/roles', role).then((res) => res.data)

export const updateJobRole = (id, role) =>
  axiosClient.put(`/admin/roles/${id}`, role).then((res) => res.data)

export const deleteJobRole = (id) => axiosClient.delete(`/admin/roles/${id}`)
