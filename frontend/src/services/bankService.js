import api from './api';

export const createAccount = (payload) => api.post('/api/accounts/create', payload).then((r) => r.data);
export const getMyAccounts = () => api.get('/api/accounts/my').then((r) => r.data);

export const deposit = (payload) => api.post('/api/transactions/deposit', payload).then((r) => r.data);
export const withdraw = (payload) => api.post('/api/transactions/withdraw', payload).then((r) => r.data);
export const transfer = (payload) => api.post('/api/transactions/transfer', payload).then((r) => r.data);
export const getTransactionHistory = (accountId) => api.get(`/api/transactions/history/${accountId}`).then((r) => r.data);

export const applyLoan = (payload) => api.post('/api/loans/apply', payload).then((r) => r.data);
export const getMyLoans = () => api.get('/api/loans/my').then((r) => r.data);

export const getAdminUsers = () => api.get('/api/admin/users').then((r) => r.data);
export const getAdminTransactions = () => api.get('/api/admin/transactions').then((r) => r.data);
export const getAdminLoans = () => api.get('/api/admin/loans').then((r) => r.data);
export const getAdminAudits = () => api.get('/api/admin/audits').then((r) => r.data);
export const approveLoan = (id, payload) => api.post(`/api/loans/${id}/approve`, payload).then((r) => r.data);
export const rejectLoan = (id, payload) => api.post(`/api/loans/${id}/reject`, payload).then((r) => r.data);

