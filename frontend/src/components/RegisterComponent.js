import React, { useState } from 'react';

function RegisterComponent() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleRegister = () => {
    console.log('Registracijos mygtukas paspaustas');
    console.log('Vartotojo vardas:', username);
    console.log('El. paštas:', email);
    console.log('Slaptažodis:', password);
    console.log('Patvirtinimo slaptažodis:', confirmPassword);
  };

  return (
    <div>
      <h2>Registracija</h2>
      <form>
        <div>
          <label>Vartotojo vardas:</label>
          <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
        </div>
        <div>
          <label>El. paštas:</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div>
          <label>Slaptažodis:</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <div>
          <label>Patvirtinti slaptažodį:</label>
          <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />
        </div>
        <button type="button" onClick={handleRegister}>Registruotis</button>
      </form>
    </div>
  );
}

export default RegisterComponent;
