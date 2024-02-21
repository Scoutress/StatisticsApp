import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import LoginComponent from './components/LoginComponent';
import RegisterComponent from './components/RegisterComponent';
import AdminPanelComponent from './components/AdminPanelComponent';
import StatisticsComponent from './components/StatisticsComponent';
import DcActivity from './components/DcActivity';

function App() {
  return (
    <Router>
      <div style={{ display: "flex" }}>
        <Sidebar />
        <div style={{ flex: 1, padding: "20px" }}>
          <Routes>
            <Route path="/login" element={<LoginComponent />} />
            <Route path="/register" element={<RegisterComponent />} />
            <Route path="/admin" element={<AdminPanelComponent />} />
            <Route path="/statistics" element={<StatisticsComponent />} />
            <Route path="/dc-activity" element={<DcActivity />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
