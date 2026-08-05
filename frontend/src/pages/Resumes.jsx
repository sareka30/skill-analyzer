import { useEffect, useState } from 'react'
import * as resumeApi from '../api/resumeApi'
import { extractErrorMessage } from '../utils/errorMessage'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'

export default function Resumes() {
  const [resumes, setResumes] = useState([])
  const [loading, setLoading] = useState(true)
  const [uploading, setUploading] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [file, setFile] = useState(null)

  const load = () => {
    setLoading(true)
    resumeApi
      .getMyResumes()
      .then(setResumes)
      .catch((err) => setError(extractErrorMessage(err, 'Could not load your resumes')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleUpload = async (e) => {
    e.preventDefault()
    if (!file) return
    setUploading(true)
    setError('')
    setMessage('')
    try {
      await resumeApi.uploadResume(file)
      setMessage('Resume uploaded and parsed successfully.')
      setFile(null)
      load()
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not upload resume'))
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className="page">
      <h1>Resumes</h1>
      <ErrorBanner message={error} />
      {message && <div className="success-banner">{message}</div>}

      <form className="upload-form" onSubmit={handleUpload}>
        <input
          type="file"
          accept=".pdf,.docx"
          onChange={(e) => setFile(e.target.files[0] || null)}
        />
        <button type="submit" disabled={!file || uploading}>
          {uploading ? 'Uploading…' : 'Upload Resume'}
        </button>
      </form>
      <p className="muted">Supported formats: PDF and DOCX, up to 5MB.</p>

      {loading ? (
        <Loading />
      ) : (
        <table className="table">
          <thead>
            <tr><th>File name</th><th>Uploaded</th><th>Extracted text</th></tr>
          </thead>
          <tbody>
            {resumes.map((r) => (
              <tr key={r.id}>
                <td>{r.fileName}</td>
                <td>{new Date(r.uploadedAt).toLocaleString()}</td>
                <td>{r.extractedTextLength} characters</td>
              </tr>
            ))}
            {resumes.length === 0 && (
              <tr><td colSpan={3} className="muted">No resumes uploaded yet.</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}
