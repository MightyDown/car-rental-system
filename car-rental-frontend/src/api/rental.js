import request from '../utils/request'

export function pickup(data) {
  return request.post('/rental/pickup', data)
}

export function returnVehicle(data) {
  return request.post('/rental/return', data)
}
