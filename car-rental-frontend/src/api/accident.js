import request from '../utils/request'

export function getAccidents(params) {
  return request.get('/accidents', { params })
}

export function getAccidentById(id) {
  return request.get(`/accidents/${id}`)
}

export function createAccident(data) {
  return request.post('/accidents', data)
}

export function processAccident(id, data) {
  return request.put(`/accidents/${id}/process`, data)
}

export function completeAccident(id) {
  return request.put(`/accidents/${id}/complete`)
}
