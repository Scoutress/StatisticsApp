import React from 'react';
import { Link } from 'react-router-dom';

function Sidebar() {
  return (
    <div style={{ padding: "20px", width: "250px", height: "100vh", background: "#f0f0f0" }}>
      <ul style={{ listStyleType: "none", padding: 0 }}>
        <li><Link to="/login">Login</Link></li>
        <li><Link to="/register">Register</Link></li>
        <li><Link to="/admin">Admin Panel</Link></li>
        <li><Link to="/statistics">Statistics</Link></li>
        <li><Link to="/dc-activity">DC Activity</Link></li>
        <li><Link to="/help-requests">Help Requests</Link></li>
      </ul>
    </div>
  );
}

export default Sidebar;
