import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { applyLoan, getMyAccounts, getMyLoans } from '../services/bankService';

export default function LoansPage() {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [loans, setLoans] = useState([]);
  const [form, setForm] = useState({
    accountId: '',
    principalAmount: '',
    annualInterestRate: '10.5',
    tenureMonths: '60',
    remarks: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const load = () => {
    getMyAccounts().then(setAccounts).catch(() => setAccounts([]));
    getMyLoans().then(setLoans).catch(() => setLoans([]));
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async (event) => {
    event.preventDefault();
    setMessage('');
    setError('');

    const payload = {
      userId: user.id,
      accountId: form.accountId ? Number(form.accountId) : null,
      principalAmount: Number(form.principalAmount),
      annualInterestRate: Number(form.annualInterestRate),
      tenureMonths: Number(form.tenureMonths),
      remarks: form.remarks
    };

    try {
      const loan = await applyLoan(payload);
      setMessage(`Loan application submitted. Estimated EMI: ${money(loan.monthlyEmi)}`);
      setForm({ accountId: '', principalAmount: '', annualInterestRate: '10.5', tenureMonths: '60', remarks: '' });
      load();
    } catch (e) {
      setError(e.response?.data?.message || 'Could not submit loan application');
    }
  };

  return (
    <section className="grid">
      <article className="card">
        <h2>Apply for Loan</h2>
        <form className="form-grid" onSubmit={submit}>
          <select value={form.accountId} onChange={(e) => setForm({ ...form, accountId: e.target.value })}>
            <option value="">No linked account</option>
            {accounts.map((account) => (
              <option key={account.id} value={account.id}>{account.accountNumber}</option>
            ))}
          </select>
          <input type="number" min="1000" step="0.01" placeholder="Principal Amount" value={form.principalAmount} onChange={(e) => setForm({ ...form, principalAmount: e.target.value })} required />
          <input type="number" min="1" step="0.01" placeholder="Annual Interest Rate" value={form.annualInterestRate} onChange={(e) => setForm({ ...form, annualInterestRate: e.target.value })} required />
          <input type="number" min="6" max="360" placeholder="Tenure Months" value={form.tenureMonths} onChange={(e) => setForm({ ...form, tenureMonths: e.target.value })} required />
          <input placeholder="Remarks" value={form.remarks} onChange={(e) => setForm({ ...form, remarks: e.target.value })} />
          {message && <p className="success">{message}</p>}
          {error && <p className="error">{error}</p>}
          <button type="submit">Submit Application</button>
        </form>
      </article>

      <article className="card">
        <h2>My Loans</h2>
        <table>
          <thead>
            <tr><th>ID</th><th>Principal</th><th>EMI</th><th>Status</th></tr>
          </thead>
          <tbody>
            {loans.map((loan) => (
              <tr key={loan.id}>
                <td>{loan.id}</td>
                <td>{money(loan.principalAmount)}</td>
                <td>{money(loan.monthlyEmi)}</td>
                <td><span className={`status ${loan.status.toLowerCase()}`}>{loan.status}</span></td>
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
