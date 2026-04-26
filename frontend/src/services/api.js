import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
});

api.interceptors.request.use((config) => {
  const raw = localStorage.getItem('bank_auth');
  if (raw) {
    try {
      const state = JSON.parse(raw);
      if (state.token) {
        config.headers.Authorization = `Bearer ${state.token}`;
      }
    } catch {
      localStorage.removeItem('bank_auth');
    }
  }
  return config;
});

export default api;

