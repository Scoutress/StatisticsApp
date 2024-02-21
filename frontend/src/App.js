import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Login from './components/Login';
import Register from './components/Register';
import AdminPanel from './components/AdminPanel';
import Statistics from './components/Statistics';
import DcActivity from './components/DcActivity';
import HelpRequests from './components/HelpRequests';
import Playtime from './components/Playtime'

function App() {
  return (
    <Router>
      <div style={{ display: "flex" }}>
        <Sidebar />
        <div style={{ flex: 1, padding: "20px" }}>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/admin" element={<AdminPanel />} />
            <Route path="/statistics" element={<Statistics />} />
            <Route path="/dc-activity" element={<DcActivity />} />
            <Route path="/help-requests" element={<HelpRequests />} />
            <Route path="/playtime" element={<Playtime />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
