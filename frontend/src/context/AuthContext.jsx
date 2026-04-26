import { createContext, useContext, useMemo, useState } from 'react';
import { loginRequest, registerRequest } from '../services/authService';

const AuthContext = createContext(null);

const STORAGE_KEY = 'bank_auth';

function loadState() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return { token: null, user: null };
  }

  try {
    return JSON.parse(raw);
  } catch {
    localStorage.removeItem(STORAGE_KEY);
    return { token: null, user: null };
  }
}

export function AuthProvider({ children }) {
  const [state, setState] = useState(loadState());

  const login = async (payload) => {
    const data = await loginRequest(payload);
    const next = {
      token: data.token,
      user: {
        id: data.userId,
        fullName: data.fullName,
        email: data.email,
        roles: data.roles
      }
    };
    setState(next);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
  };

  const register = async (payload) => {
    const data = await registerRequest(payload);
    const next = {
      token: data.token,
      user: {
        id: data.userId,
        fullName: data.fullName,
        email: data.email,
        roles: data.roles
      }
    };
    setState(next);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
  };

  const logout = () => {
    setState({ token: null, user: null });
    localStorage.removeItem(STORAGE_KEY);
  };

  const value = useMemo(() => ({
    ...state,
    isAuthenticated: Boolean(state.token),
    login,
    register,
    logout
  }), [state]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return value;
}

