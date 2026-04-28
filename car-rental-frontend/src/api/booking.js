import request from '../utils/request'

export function getBookings(params) {
  return request.get('/bookings', { params })
}

export function getMyBookings(params) {
  return request.get('/bookings/my', { params })
}

export function getBookingById(id) {
  return request.get(`/bookings/${id}`)
}

export function createBooking(data) {
  return request.post('/bookings', data)
}

export function confirmBooking(id) {
  return request.put(`/bookings/${id}/confirm`)
}

export function cancelBooking(id) {
  return request.put(`/bookings/${id}/cancel`)
}
