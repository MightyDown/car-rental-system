import request from '../utils/request'

export function getVehicles(params) {
  return request.get('/vehicles', { params })
}

export function getVehicleById(id) {
  return request.get(`/vehicles/${id}`)
}

export function createVehicle(data) {
  return request.post('/vehicles', data)
}

export function updateVehicle(id, data) {
  return request.put(`/vehicles/${id}`, data)
}

export function updateVehicleStatus(id, status) {
  return request.put(`/vehicles/${id}/status`, null, { params: { status } })
}

export function deleteVehicle(id) {
  return request.delete(`/vehicles/${id}`)
}

export function getConfigs() {
  return request.get('/vehicle-configs')
}

export function updateConfig(id, data) {
  return request.put(`/vehicle-configs/${id}`, data)
}
