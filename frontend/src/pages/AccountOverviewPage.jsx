import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { createAccount, getMyAccounts } from '../services/bankService';

export default function AccountOverviewPage() {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [ownerUserId, setOwnerUserId] = useState('');
  const [accountType, setAccountType] = useState('SAVINGS');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const canCreateAccount = user?.roles?.some((role) => ['ADMIN', 'STAFF'].includes(role));

  const load = () => getMyAccounts().then(setAccounts).catch(() => setAccounts([]));
  useEffect(() => {
    load();
  }, []);

  const submit = async (event) => {
    event.preventDefault();
    setMessage('');
    setError('');
    try {
      const account = await createAccount({ ownerUserId: Number(ownerUserId), accountType });
      setOwnerUserId('');
      setMessage(`Created account ${account.accountNumber}`);
      load();
    } catch (e) {
      setError(e.response?.data?.message || 'Could not create account');
    }
  };

  return (
    <section className="card">
      <h2>Account Overview</h2>
      {canCreateAccount && (
        <form className="row" onSubmit={submit}>
          <input value={ownerUserId} onChange={(e) => setOwnerUserId(e.target.value)} placeholder="Owner User ID" required />
          <select value={accountType} onChange={(e) => setAccountType(e.target.value)}>
            <option value="SAVINGS">Savings</option>
            <option value="CURRENT">Current</option>
          </select>
          <button type="submit">Create Account</button>
        </form>
      )}
      {message && <p className="success">{message}</p>}
      {error && <p className="error">{error}</p>}
      <table>
        <thead>
          <tr><th>ID</th><th>Number</th><th>Type</th><th>Balance</th></tr>
        </thead>
        <tbody>
          {accounts.map((a) => (
            <tr key={a.id}>
              <td>{a.id}</td><td>{a.accountNumber}</td><td>{a.accountType}</td><td>{a.balance}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

