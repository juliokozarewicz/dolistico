import { Routes, Route } from 'react-router-dom';

import UpdatePassword from './pages/update-password';
import UpdateEmail from './pages/update-email';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/update-password" element={<UpdatePassword />} />
      <Route path="/update-email" element={<UpdateEmail />} />
    </Routes>
  );
}