import request from '../utils/request'

export function getUsers(params) {
  return request.get('/users', { params })
}

export function getUserById(id) {
  return request.get(`/users/${id}`)
}

export function getMe() {
  return request.get('/users/me')
}

export function updateMe(data) {
  return request.put('/users/me', data)
}

export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

export function updateUserStatus(id, status) {
  return request.put(`/users/${id}/status`, null, { params: { status } })
}

export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}
