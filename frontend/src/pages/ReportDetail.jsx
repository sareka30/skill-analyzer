import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import * as reportApi from '../api/reportApi'
import { extractErrorMessage } from '../utils/errorMessage'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'

export default function ReportDetail() {
  const { id } = useParams()
  const [report, setReport] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    reportApi
      .getReport(id)
      .then(setReport)
      .catch((err) => setError(extractErrorMessage(err, 'Could not load report')))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loading label="Loading report…" />
  if (error) return <div className="page"><ErrorBanner message={error} /></div>
  if (!report) return null

  return (
    <div className="page">
      <p><Link to="/reports">&larr; Back to reports</Link></p>
      <h1>{report.jobRoleTitle}</h1>
      <p className="muted">{report.jobRoleDescription}</p>

      <div className="score-row">
        <div className="score-box">
          <span className="score-value">{report.matchPercentage}%</span>
          <span className="score-label">Skill Match</span>
        </div>
        <div className="score-box">
          <span className="score-value">{report.employabilityScore}</span>
          <span className="score-label">Employability Score</span>
        </div>
      </div>

      <h2>Matched Skills</h2>
      <div className="chip-list">
        {report.matchedSkills.map((s) => (
          <span key={s.id} className="chip chip-selected">{s.name}</span>
        ))}
        {report.matchedSkills.length === 0 && <p className="muted">None yet.</p>}
      </div>

      <h2>Missing Skills</h2>
      <div className="chip-list">
        {report.missingSkills.map((s) => (
          <span key={s.id} className="chip chip-static">{s.name}</span>
        ))}
        {report.missingSkills.length === 0 && <p className="muted">Great job — no gaps found!</p>}
      </div>

      <h2>Recommendations</h2>
      <div className="card-grid">
        {report.recommendations.map((rec) => (
          <div key={rec.id} className="card">
            <span className="badge">{rec.type}</span>
            <h3>{rec.title}</h3>
            <p className="muted">{rec.providerOrPlatform}</p>
            <p>{rec.description}</p>
            {rec.url && <a href={rec.url} target="_blank" rel="noreferrer">Learn more &rarr;</a>}
          </div>
        ))}
        {report.recommendations.length === 0 && <p className="muted">No recommendations needed.</p>}
      </div>
    </div>
  )
}
