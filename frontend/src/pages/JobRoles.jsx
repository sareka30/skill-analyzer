import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import * as jobRoleApi from '../api/jobRoleApi'
import { extractErrorMessage } from '../utils/errorMessage'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'

export default function JobRoles() {
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    jobRoleApi
      .getJobRoles()
      .then(setRoles)
      .catch((err) => setError(extractErrorMessage(err, 'Could not load job roles')))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Loading label="Loading job roles…" />

  return (
    <div className="page">
      <h1>Job Roles</h1>
      <ErrorBanner message={error} />
      <div className="card-grid">
        {roles.map((role) => (
          <div key={role.id} className="card">
            <h3>{role.title}</h3>
            <p className="muted">{role.description}</p>
            <div className="chip-list">
              {(role.requiredSkills || []).map((s) => (
                <span key={s.id} className="chip chip-static">{s.name}</span>
              ))}
            </div>
          </div>
        ))}
      </div>
      <p className="muted">
        Ready to see how you match up? Head to <Link to="/resumes">Resumes</Link> to upload a resume, then
        generate a report from <Link to="/reports">Reports</Link>.
      </p>
    </div>
  )
}
