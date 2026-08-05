import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import * as reportApi from '../api/reportApi'
import * as resumeApi from '../api/resumeApi'
import * as jobRoleApi from '../api/jobRoleApi'
import { extractErrorMessage } from '../utils/errorMessage'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'

export default function Reports() {
  const [reports, setReports] = useState([])
  const [resumes, setResumes] = useState([])
  const [roles, setRoles] = useState([])
  const [resumeId, setResumeId] = useState('')
  const [jobRoleId, setJobRoleId] = useState('')
  const [loading, setLoading] = useState(true)
  const [generating, setGenerating] = useState(false)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    Promise.all([reportApi.getMyReports(), resumeApi.getMyResumes(), jobRoleApi.getJobRoles()])
      .then(([r, res, roleList]) => {
        setReports(r)
        setResumes(res)
        setRoles(roleList)
      })
      .catch((err) => setError(extractErrorMessage(err, 'Could not load reports')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleGenerate = async (e) => {
    e.preventDefault()
    if (!resumeId || !jobRoleId) return
    setGenerating(true)
    setError('')
    try {
      await reportApi.generateReport(resumeId, jobRoleId)
      load()
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not generate report'))
    } finally {
      setGenerating(false)
    }
  }

  if (loading) return <Loading label="Loading reports…" />

  return (
    <div className="page">
      <h1>Skill Gap Reports</h1>
      <ErrorBanner message={error} />

      <form className="inline-form" onSubmit={handleGenerate}>
        <label>
          Resume
          <select value={resumeId} onChange={(e) => setResumeId(e.target.value)} required>
            <option value="">Select a resume…</option>
            {resumes.map((r) => (
              <option key={r.id} value={r.id}>{r.fileName}</option>
            ))}
          </select>
        </label>
        <label>
          Target role
          <select value={jobRoleId} onChange={(e) => setJobRoleId(e.target.value)} required>
            <option value="">Select a job role…</option>
            {roles.map((r) => (
              <option key={r.id} value={r.id}>{r.title}</option>
            ))}
          </select>
        </label>
        <button type="submit" disabled={generating || resumes.length === 0}>
          {generating ? 'Generating…' : 'Generate Report'}
        </button>
      </form>
      {resumes.length === 0 && (
        <p className="muted">Upload a resume first on the <Link to="/resumes">Resumes</Link> page.</p>
      )}

      <div className="card-grid">
        {reports.map((r) => (
          <Link key={r.id} to={`/reports/${r.id}`} className="card card-link">
            <h3>{r.jobRoleTitle}</h3>
            <p>Match: <b>{r.matchPercentage}%</b></p>
            <p>Employability score: <b>{r.employabilityScore}</b></p>
            <p className="muted">{new Date(r.generatedAt).toLocaleString()}</p>
          </Link>
        ))}
        {reports.length === 0 && <p className="muted">No reports generated yet.</p>}
      </div>
    </div>
  )
}
