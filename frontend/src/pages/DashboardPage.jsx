import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { getMyAccounts, getMyLoans } from '../services/bankService';

export default function DashboardPage() {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [loans, setLoans] = useState([]);

  useEffect(() => {
    getMyAccounts().then(setAccounts).catch(() => setAccounts([]));
    getMyLoans().then(setLoans).catch(() => setLoans([]));
  }, []);

  const totalBalance = accounts.reduce((acc, a) => acc + Number(a.balance), 0);

  return (
    <section className="grid">
      <article className="card">
        <h2>Welcome, {user?.fullName}</h2>
        <p>Email: {user?.email}</p>
        <p>Roles: {user?.roles?.join(', ')}</p>
      </article>
      <article className="card">
        <h3>Account Summary</h3>
        <p>Accounts: {accounts.length}</p>
        <p>Total Balance: {totalBalance.toFixed(2)}</p>
      </article>
      <article className="card">
        <h3>Loans</h3>
        <p>Total Applications: {loans.length}</p>
        <p>Pending: {loans.filter((l) => l.status === 'PENDING').length}</p>
      </article>
    </section>
  );
}

