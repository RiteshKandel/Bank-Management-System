import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <header className="navbar">
      <div className="brand">Bank Management</div>
      <nav>
        {isAuthenticated ? (
          <>
            <Link to="/">Dashboard</Link>
            <Link to="/accounts">Accounts</Link>
            <Link to="/transfer">Money</Link>
            <Link to="/transactions">History</Link>
            <Link to="/loans">Loans</Link>
            {user?.roles?.some((role) => ['ADMIN', 'STAFF'].includes(role)) && <Link to="/admin">Admin</Link>}
            <button type="button" onClick={logout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}

