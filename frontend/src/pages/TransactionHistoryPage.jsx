import { useState } from 'react';
import { getTransactionHistory } from '../services/bankService';

export default function TransactionHistoryPage() {
  const [accountId, setAccountId] = useState('');
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');

  const load = async (event) => {
    event.preventDefault();
    setError('');
    try {
      const data = await getTransactionHistory(Number(accountId));
      setTransactions(data);
    } catch (e) {
      setTransactions([]);
      setError(e.response?.data?.message || 'Could not load transactions');
    }
  };

  return (
    <section className="card">
      <h2>Transaction History</h2>
      <form className="row" onSubmit={load}>
        <input placeholder="Account ID" value={accountId} onChange={(e) => setAccountId(e.target.value)} required />
        <button type="submit">Load</button>
      </form>
      {error && <p className="error">{error}</p>}
      <table>
        <thead>
          <tr><th>Ref</th><th>Type</th><th>Amount</th><th>Date</th></tr>
        </thead>
        <tbody>
          {transactions.map((tx) => (
            <tr key={tx.id}>
              <td>{tx.referenceNumber}</td>
              <td>{tx.transactionType}</td>
              <td>{tx.amount}</td>
              <td>{new Date(tx.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

