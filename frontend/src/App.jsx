import React from 'react';
import { Router, Route, Routes, useNavigate } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import AdminPanel from './components/AdminPanel';
import Statistics from './components/Statistics';
import DcActivity from './components/DcActivity';
import HelpRequests from './components/HelpRequests';
import Playtime from './components/Playtime'
import { oktaConfig } from './lib/oktaConfig';
import { OktaAuth, toRelativeUrl } from '@okta/okta-auth-js';

const oktaAuth = new OktaAuth(oktaConfig);

function App() {

  const customAuthHandler = () => {
    navigate.push('/login')
  }

  const navigate = useNavigate();

  const restoreOriginalUri = async (_oktaAuth: any, originalUri: any) => {
    navigate.replace(toRelativeUrl(originalUri || '/', window.location.origin));
  };

  return (
    <Router>
      <div style={{ display: "flex" }}>
        <Sidebar />
        <div style={{ flex: 1, padding: "20px" }}>
          <Routes>
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
