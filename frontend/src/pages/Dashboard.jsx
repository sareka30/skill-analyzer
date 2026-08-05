import { useEffect, useState } from 'react'
import * as authApi from '../api/authApi'
import * as skillApi from '../api/skillApi'
import { extractErrorMessage } from '../utils/errorMessage'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'

export default function Dashboard() {
  const [profile, setProfile] = useState(null)
  const [allSkills, setAllSkills] = useState([])
  const [selectedSkillIds, setSelectedSkillIds] = useState(new Set())
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  useEffect(() => {
    Promise.all([authApi.getProfile(), skillApi.getSkills()])
      .then(([profileData, skills]) => {
        setProfile(profileData)
        setAllSkills(skills)
        setSelectedSkillIds(new Set((profileData.skills || []).map((s) => s.id)))
      })
      .catch((err) => setError(extractErrorMessage(err, 'Could not load your profile')))
      .finally(() => setLoading(false))
  }, [])

  const toggleSkill = (id) => {
    setSelectedSkillIds((prev) => {
      const next = new Set(prev)
      next.has(id) ? next.delete(id) : next.add(id)
      return next
    })
  }

  const handleSave = async () => {
    setSaving(true)
    setError('')
    setMessage('')
    try {
      const updated = await authApi.updateMySkills(Array.from(selectedSkillIds))
      setProfile(updated)
      setMessage('Your skills were saved.')
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not save your skills'))
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loading label="Loading your dashboard…" />

  const grouped = allSkills.reduce((acc, skill) => {
    acc[skill.category] = acc[skill.category] || []
    acc[skill.category].push(skill)
    return acc
  }, {})

  return (
    <div className="page">
      <h1>Welcome, {profile?.username}</h1>
      <p className="muted">{profile?.email} · {profile?.role}</p>
      <ErrorBanner message={error} />
      {message && <div className="success-banner">{message}</div>}

      <h2>My Skills</h2>
      <p className="muted">Select the skills you already have. This is used to fill any gaps your resume text might miss.</p>
      {Object.entries(grouped).map(([category, skills]) => (
        <div key={category} className="skill-group">
          <h3>{category}</h3>
          <div className="chip-list">
            {skills.map((skill) => (
              <button
                key={skill.id}
                type="button"
                className={`chip ${selectedSkillIds.has(skill.id) ? 'chip-selected' : ''}`}
                onClick={() => toggleSkill(skill.id)}
              >
                {skill.name}
              </button>
            ))}
          </div>
        </div>
      ))}
      <button onClick={handleSave} disabled={saving}>{saving ? 'Saving…' : 'Save Skills'}</button>
    </div>
  )
}
