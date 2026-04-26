import api from './api';

export async function loginRequest(payload) {
  const { data } = await api.post('/api/auth/login', payload);
  return data;
}

export async function registerRequest(payload) {
  const { data } = await api.post('/api/auth/register', payload);
  return data;
}

