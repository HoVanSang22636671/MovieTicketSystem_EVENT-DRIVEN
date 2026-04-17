import React, { useEffect, useMemo, useState } from 'react';
import { api } from './api.js';

export default function App() {
  const [registerUsername, setRegisterUsername] = useState('thai');
  const [registerPassword, setRegisterPassword] = useState('123');
  const [loginUsername, setLoginUsername] = useState('thai');
  const [loginPassword, setLoginPassword] = useState('123');

  const [user, setUser] = useState(null);
  const [movies, setMovies] = useState([]);
  const [selectedMovieId, setSelectedMovieId] = useState('');
  const selectedMovie = useMemo(
    () => movies.find((m) => m.id === selectedMovieId) || null,
    [movies, selectedMovieId]
  );

  const [seatNumber, setSeatNumber] = useState('A5');
  const [bookings, setBookings] = useState([]);
  const [message, setMessage] = useState('');

  async function loadMovies() {
    const res = await api.get('/movies');
    setMovies(res.data);
    if (!selectedMovieId && res.data?.length) {
      setSelectedMovieId(res.data[0].id);
    }
  }

  async function loadBookings() {
    const res = await api.get('/bookings');
    setBookings(res.data);
  }

  useEffect(() => {
    loadMovies().catch((e) => setMessage(e?.message || 'Failed to load movies'));
    loadBookings().catch(() => {});
  }, []);

  async function onRegister(e) {
    e.preventDefault();
    setMessage('');
    try {
      const res = await api.post('/users/register', {
        username: registerUsername,
        password: registerPassword,
      });
      setMessage(`Registered userId=${res.data.id}`);
    } catch (err) {
      setMessage(err?.response?.data?.message || err?.message || 'Register failed');
    }
  }

  async function onLogin(e) {
    e.preventDefault();
    setMessage('');
    try {
      const res = await api.post('/users/login', {
        username: loginUsername,
        password: loginPassword,
      });
      setUser(res.data);
      setMessage(`Logged in userId=${res.data.id}`);
    } catch (err) {
      setMessage(err?.response?.data?.message || err?.message || 'Login failed');
    }
  }

  async function onCreateBooking(e) {
    e.preventDefault();
    setMessage('');
    if (!user?.id) {
      setMessage('Please login first');
      return;
    }
    if (!selectedMovie) {
      setMessage('No movie selected');
      return;
    }

    try {
      const res = await api.post('/bookings', {
        userId: user.id,
        movieId: selectedMovie.id,
        seatNumber,
        amount: selectedMovie.price,
      });

      setMessage(`Booking created id=${res.data.id} status=${res.data.status}`);
      await loadBookings();
    } catch (err) {
      setMessage(err?.response?.data?.message || err?.message || 'Create booking failed');
    }
  }

  return (
    <div style={{ fontFamily: 'system-ui, sans-serif', padding: 16, maxWidth: 900, margin: '0 auto' }}>
      <h1>Movie Ticket System (EDA Demo)</h1>

      {message ? (
        <div style={{ padding: 12, background: '#f2f2f2', borderRadius: 8, marginBottom: 16 }}>{message}</div>
      ) : null}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginBottom: 24 }}>
        <form onSubmit={onRegister} style={{ padding: 12, border: '1px solid #ddd', borderRadius: 8 }}>
          <h2>Register</h2>
          <div style={{ display: 'grid', gap: 8 }}>
            <input value={registerUsername} onChange={(e) => setRegisterUsername(e.target.value)} placeholder="username" />
            <input value={registerPassword} onChange={(e) => setRegisterPassword(e.target.value)} placeholder="password" type="password" />
            <button type="submit">Register</button>
          </div>
        </form>

        <form onSubmit={onLogin} style={{ padding: 12, border: '1px solid #ddd', borderRadius: 8 }}>
          <h2>Login</h2>
          <div style={{ display: 'grid', gap: 8 }}>
            <input value={loginUsername} onChange={(e) => setLoginUsername(e.target.value)} placeholder="username" />
            <input value={loginPassword} onChange={(e) => setLoginPassword(e.target.value)} placeholder="password" type="password" />
            <button type="submit">Login</button>
            <div style={{ color: '#555' }}>Current: {user ? `${user.username} (${user.id})` : 'Not logged in'}</div>
          </div>
        </form>
      </div>

      <div style={{ padding: 12, border: '1px solid #ddd', borderRadius: 8, marginBottom: 24 }}>
        <h2>Movies</h2>
        <div style={{ display: 'grid', gap: 8 }}>
          <button type="button" onClick={() => loadMovies().catch(() => setMessage('Failed to load movies'))}>
            Refresh movies
          </button>
          <select value={selectedMovieId} onChange={(e) => setSelectedMovieId(e.target.value)}>
            {movies.map((m) => (
              <option key={m.id} value={m.id}>
                {m.title} — {m.price}
              </option>
            ))}
          </select>
          {selectedMovie ? (
            <div style={{ color: '#555' }}>
              Selected: {selectedMovie.title} ({selectedMovie.durationMinutes} min) — {selectedMovie.price}
            </div>
          ) : null}
        </div>
      </div>

      <form onSubmit={onCreateBooking} style={{ padding: 12, border: '1px solid #ddd', borderRadius: 8, marginBottom: 24 }}>
        <h2>Create booking</h2>
        <div style={{ display: 'grid', gap: 8 }}>
          <input value={seatNumber} onChange={(e) => setSeatNumber(e.target.value)} placeholder="seatNumber (e.g., A5)" />
          <button type="submit">Create booking (async payment)</button>
        </div>
      </form>

      <div style={{ padding: 12, border: '1px solid #ddd', borderRadius: 8 }}>
        <h2>Bookings</h2>
        <div style={{ display: 'flex', gap: 8, marginBottom: 8 }}>
          <button type="button" onClick={() => loadBookings().catch(() => setMessage('Failed to load bookings'))}>
            Refresh bookings
          </button>
          <div style={{ color: '#555' }}>Payment result will update status after ~1-2s</div>
        </div>

        <table width="100%" cellPadding={8} style={{ borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th align="left">BookingId</th>
              <th align="left">UserId</th>
              <th align="left">MovieId</th>
              <th align="left">Seat</th>
              <th align="left">Amount</th>
              <th align="left">Status</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map((b) => (
              <tr key={b.id} style={{ borderTop: '1px solid #eee' }}>
                <td>{b.id}</td>
                <td>{b.userId}</td>
                <td>{b.movieId}</td>
                <td>{b.seatNumber}</td>
                <td>{b.amount}</td>
                <td>{b.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
