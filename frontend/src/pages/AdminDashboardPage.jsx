import { useEffect, useState } from 'react';
import { approveLoan, getAdminAudits, getAdminLoans, getAdminTransactions, getAdminUsers, rejectLoan } from '../services/bankService';

export default function AdminDashboardPage() {
  const [users, setUsers] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [audits, setAudits] = useState([]);
  const [loans, setLoans] = useState([]);
  const [decisionRemark, setDecisionRemark] = useState('');
  const [message, setMessage] = useState('');

  const load = () => {
    getAdminUsers().then(setUsers).catch(() => setUsers([]));
    getAdminTransactions().then(setTransactions).catch(() => setTransactions([]));
    getAdminLoans().then(setLoans).catch(() => setLoans([]));
    getAdminAudits().then(setAudits).catch(() => setAudits([]));
  };

  useEffect(() => {
    load();
  }, []);

  const decide = async (loanId, action) => {
    const payload = { remarks: decisionRemark || `${action} by staff` };
    const updated = action === 'Approved' ? await approveLoan(loanId, payload) : await rejectLoan(loanId, payload);
    setMessage(`Loan ${updated.id} ${updated.status.toLowerCase()}`);
    setDecisionRemark('');
    load();
  };

  return (
    <section className="grid">
      <article className="card">
        <h2>Admin Dashboard</h2>
        <p>Total Users: {users.length}</p>
        <p>Total Transactions: {transactions.length}</p>
        <p>Loan Applications: {loans.length}</p>
        <p>Audit Records: {audits.length}</p>
        {message && <p className="success">{message}</p>}
      </article>
      <article className="card">
        <h3>Users</h3>
        <ul>
          {users.slice(0, 10).map((u) => <li key={u.id}>{u.fullName} ({u.email})</li>)}
        </ul>
      </article>
      <article className="card">
        <h3>Recent Transactions</h3>
        <ul>
          {transactions.slice(0, 10).map((t) => <li key={t.id}>{t.referenceNumber} - {t.amount}</li>)}
        </ul>
      </article>
      <article className="card full-width">
        <h3>Loan Review</h3>
        <div className="row">
          <input placeholder="Decision remarks" value={decisionRemark} onChange={(e) => setDecisionRemark(e.target.value)} />
        </div>
        <table>
          <thead>
            <tr><th>ID</th><th>User</th><th>Principal</th><th>EMI</th><th>Status</th><th>Action</th></tr>
          </thead>
          <tbody>
            {loans.slice(0, 20).map((loan) => (
              <tr key={loan.id}>
                <td>{loan.id}</td>
                <td>{loan.userId}</td>
                <td>{money(loan.principalAmount)}</td>
                <td>{money(loan.monthlyEmi)}</td>
                <td><span className={`status ${loan.status.toLowerCase()}`}>{loan.status}</span></td>
                <td>
                  {loan.status === 'PENDING' ? (
                    <div className="table-actions">
                      <button type="button" onClick={() => decide(loan.id, 'Approved')}>Approve</button>
                      <button type="button" className="danger" onClick={() => decide(loan.id, 'Rejected')}>Reject</button>
                    </div>
                  ) : (
                    'Reviewed'
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </article>
    </section>
  );
}

function money(value) {
  return Number(value || 0).toFixed(2);
}

