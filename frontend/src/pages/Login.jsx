import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { extractErrorMessage } from '../utils/errorMessage'
import ErrorBanner from '../components/ErrorBanner'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const user = await login(username, password)
      navigate(user.role === 'ADMIN' ? '/admin' : '/dashboard')
    } catch (err) {
      setError(extractErrorMessage(err, 'Invalid username or password'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h2>Login</h2>
        <ErrorBanner message={error} />
        <label>
          Username
          <input value={username} onChange={(e) => setUsername(e.target.value)} required autoFocus />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>
        <button type="submit" disabled={submitting}>{submitting ? 'Logging in…' : 'Login'}</button>
        <p>Don't have an account? <Link to="/register">Register</Link></p>
        <p className="hint">Demo admin account: <b>admin</b> / <b>admin123</b></p>
      </form>
    </div>
  )
}
