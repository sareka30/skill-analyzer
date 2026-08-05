import axiosClient from './axiosClient'

export const uploadResume = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return axiosClient
    .post('/student/resumes/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    .then((res) => res.data)
}

export const getMyResumes = () => axiosClient.get('/student/resumes').then((res) => res.data)
