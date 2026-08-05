import { useEffect, useState } from 'react'
import * as skillApi from '../api/skillApi'
import * as jobRoleApi from '../api/jobRoleApi'
import { extractErrorMessage } from '../utils/errorMessage'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'

export default function Admin() {
  const [skills, setSkills] = useState([])
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [skillName, setSkillName] = useState('')
  const [skillCategory, setSkillCategory] = useState('Technical')

  const [roleTitle, setRoleTitle] = useState('')
  const [roleDescription, setRoleDescription] = useState('')
  const [roleSkillIds, setRoleSkillIds] = useState(new Set())

  const load = () => {
    setLoading(true)
    Promise.all([skillApi.getSkills(), jobRoleApi.getJobRoles()])
      .then(([s, r]) => {
        setSkills(s)
        setRoles(r)
      })
      .catch((err) => setError(extractErrorMessage(err, 'Could not load admin data')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleAddSkill = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await skillApi.createSkill({ name: skillName, category: skillCategory })
      setSkillName('')
      load()
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not create skill'))
    }
  }

  const handleDeleteSkill = async (id) => {
    setError('')
    try {
      await skillApi.deleteSkill(id)
      load()
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not delete skill'))
    }
  }

  const toggleRoleSkill = (id) => {
    setRoleSkillIds((prev) => {
      const next = new Set(prev)
      next.has(id) ? next.delete(id) : next.add(id)
      return next
    })
  }

  const handleAddRole = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await jobRoleApi.createJobRole({
        title: roleTitle,
        description: roleDescription,
        skillIds: Array.from(roleSkillIds),
      })
      setRoleTitle('')
      setRoleDescription('')
      setRoleSkillIds(new Set())
      load()
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not create job role'))
    }
  }

  const handleDeleteRole = async (id) => {
    setError('')
    try {
      await jobRoleApi.deleteJobRole(id)
      load()
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not delete job role'))
    }
  }

  if (loading) return <Loading label="Loading admin panel…" />

  return (
    <div className="page">
      <h1>Admin</h1>
      <ErrorBanner message={error} />

      <section>
        <h2>Skills</h2>
        <form className="inline-form" onSubmit={handleAddSkill}>
          <input placeholder="Skill name" value={skillName} onChange={(e) => setSkillName(e.target.value)} required />
          <select value={skillCategory} onChange={(e) => setSkillCategory(e.target.value)}>
            <option>Technical</option>
            <option>Tools</option>
            <option>Soft</option>
          </select>
          <button type="submit">Add Skill</button>
        </form>
        <table className="table">
          <thead><tr><th>Name</th><th>Category</th><th></th></tr></thead>
          <tbody>
            {skills.map((s) => (
              <tr key={s.id}>
                <td>{s.name}</td>
                <td>{s.category}</td>
                <td><button className="link-button danger" onClick={() => handleDeleteSkill(s.id)}>Delete</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      <section>
        <h2>Job Roles</h2>
        <form className="stacked-form" onSubmit={handleAddRole}>
          <input placeholder="Role title" value={roleTitle} onChange={(e) => setRoleTitle(e.target.value)} required />
          <textarea placeholder="Description" value={roleDescription} onChange={(e) => setRoleDescription(e.target.value)} />
          <div className="chip-list">
            {skills.map((s) => (
              <button
                type="button"
                key={s.id}
                className={`chip ${roleSkillIds.has(s.id) ? 'chip-selected' : ''}`}
                onClick={() => toggleRoleSkill(s.id)}
              >
                {s.name}
              </button>
            ))}
          </div>
          <button type="submit">Add Job Role</button>
        </form>

        <div className="card-grid">
          {roles.map((r) => (
            <div key={r.id} className="card">
              <h3>{r.title}</h3>
              <p className="muted">{r.description}</p>
              <div className="chip-list">
                {(r.requiredSkills || []).map((s) => (
                  <span key={s.id} className="chip chip-static">{s.name}</span>
                ))}
              </div>
              <button className="link-button danger" onClick={() => handleDeleteRole(r.id)}>Delete role</button>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
