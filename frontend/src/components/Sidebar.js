import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const admins = [
  { id: 1, name: 'Admin1', email: 'admin1@example.com' },
  { id: 2, name: 'Admin2', email: 'admin2@example.com' },
];

const Sidebar = () => {
  const [isOpen, setIsOpen] = useState(false);

  const handleToggle = () => {
    setIsOpen(!isOpen);
  }

  return (
    <div style={{ padding: "20px", width: "250px", height: "100vh", background: "#f0f0f0" }}>
      <ul style={{ listStyleType: "none", padding: 0 }}>
        <li><Link to="/login">Login</Link></li>
        <li><Link to="/register">Register</Link></li>
        <li><Link to="/admin">Admin Panel</Link></li>
        <li><Link to="/statistics">Statistics</Link></li>
        <li><Link to="/dc-activity">DC Activity</Link></li>
        <li><Link to="/help-requests">Help Requests</Link></li>
        <li><Link to="/playtime">Playtime</Link></li>
        <li>
          <span onClick={handleToggle} style={{ cursor: 'pointer' }}>Admins List</span>
          {isOpen && (
            <ul>
              {admins.map(admin => (
                <li key={admin.id}><Link to={`/admin/${admin.id}`}>{admin.name}</Link></li>
              ))}
            </ul>
          )}
        </li>
      </ul>
    </div>
  );
}

export default Sidebar;
