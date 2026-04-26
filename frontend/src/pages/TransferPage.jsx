import { useState } from 'react';
import { deposit, transfer, withdraw } from '../services/bankService';

export default function TransferPage() {
  const [operation, setOperation] = useState({ accountId: '', amount: '', description: '' });
  const [form, setForm] = useState({ fromAccountId: '', toAccountId: '', amount: '', description: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const submitOperation = async (event, action) => {
    event.preventDefault();
    setMessage('');
    setError('');
    const payload = {
      accountId: Number(operation.accountId),
      amount: Number(operation.amount),
      description: operation.description
    };

    try {
      const result = action === 'deposit' ? await deposit(payload) : await withdraw(payload);
      setMessage(`${action === 'deposit' ? 'Deposit' : 'Withdrawal'} complete. Ref: ${result.referenceNumber}`);
      setOperation({ accountId: '', amount: '', description: '' });
    } catch (e) {
      setError(e.response?.data?.message || 'Transaction failed');
    }
  };

  const submitTransfer = async (event) => {
    event.preventDefault();
    setMessage('');
    setError('');
    const payload = {
      fromAccountId: Number(form.fromAccountId),
      toAccountId: Number(form.toAccountId),
      amount: Number(form.amount),
      description: form.description
    };
    try {
      const result = await transfer(payload);
      setMessage(`Transfer complete. Ref: ${result.referenceNumber}`);
      setForm({ fromAccountId: '', toAccountId: '', amount: '', description: '' });
    } catch (e) {
      setError(e.response?.data?.message || 'Transfer failed');
    }
  };

  return (
    <section className="grid">
      <article className="card">
        <h2>Deposit or Withdraw</h2>
        <form className="form-grid">
          <input type="number" min="1" placeholder="Account ID" value={operation.accountId} onChange={(e) => setOperation({ ...operation, accountId: e.target.value })} required />
          <input type="number" min="0.01" step="0.01" placeholder="Amount" value={operation.amount} onChange={(e) => setOperation({ ...operation, amount: e.target.value })} required />
          <input placeholder="Description" value={operation.description} onChange={(e) => setOperation({ ...operation, description: e.target.value })} />
          <div className="button-row">
            <button type="submit" onClick={(event) => submitOperation(event, 'deposit')}>Deposit</button>
            <button type="submit" className="secondary" onClick={(event) => submitOperation(event, 'withdraw')}>Withdraw</button>
          </div>
        </form>
      </article>

      <article className="card">
        <h2>Transfer Money</h2>
        <form className="form-grid" onSubmit={submitTransfer}>
          <input type="number" min="1" placeholder="From Account ID" value={form.fromAccountId} onChange={(e) => setForm({ ...form, fromAccountId: e.target.value })} required />
          <input type="number" min="1" placeholder="To Account ID" value={form.toAccountId} onChange={(e) => setForm({ ...form, toAccountId: e.target.value })} required />
          <input type="number" min="0.01" step="0.01" placeholder="Amount" value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} required />
          <input placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <button type="submit">Transfer</button>
        </form>
      </article>

      <article className="card full-width">
        {message && <p className="success">{message}</p>}
        {error && <p className="error">{error}</p>}
      </article>
    </section>
  );
}

