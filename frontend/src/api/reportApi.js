import axiosClient from './axiosClient'

export const generateReport = (resumeId, jobRoleId) =>
  axiosClient
    .post(`/student/reports/generate?resumeId=${resumeId}&jobRoleId=${jobRoleId}`)
    .then((res) => res.data)

export const getMyReports = () => axiosClient.get('/student/reports').then((res) => res.data)

export const getReport = (id) => axiosClient.get(`/student/reports/${id}`).then((res) => res.data)
