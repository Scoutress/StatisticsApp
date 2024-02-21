import React, { useState } from 'react';

function LoginComponent() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = () => {
    console.log('Prisijungimo mygtukas paspaustas');
    console.log('Vartotojo vardas:', username);
    console.log('Slaptažodis:', password);
  };

  return (
    <div>
      <h2>Prisijungimas</h2>
      <form>
        <div>
          <label>Vartotojo vardas:</label>
          <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
        </div>
        <div>
          <label>Slaptažodis:</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <button type="button" onClick={handleLogin}>Prisijungti</button>
      </form>
    </div>
  );
}

export default LoginComponent;
