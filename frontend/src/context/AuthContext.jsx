import { createContext, useContext, useState, useCallback } from 'react'
import * as authApi from '../api/authApi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('sga_user')
    return stored ? JSON.parse(stored) : null
  })

  const login = useCallback(async (username, password) => {
    const data = await authApi.login(username, password)
    const loggedInUser = { id: data.id, username: data.username, email: data.email, role: data.role }
    localStorage.setItem('sga_token', data.token)
    localStorage.setItem('sga_user', JSON.stringify(loggedInUser))
    setUser(loggedInUser)
    return loggedInUser
  }, [])

  const register = useCallback(async (username, email, password) => {
    const data = await authApi.register(username, email, password)
    const registeredUser = { id: data.id, username: data.username, email: data.email, role: data.role }
    localStorage.setItem('sga_token', data.token)
    localStorage.setItem('sga_user', JSON.stringify(registeredUser))
    setUser(registeredUser)
    return registeredUser
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('sga_token')
    localStorage.removeItem('sga_user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isAdmin: user?.role === 'ADMIN' }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return ctx
}
