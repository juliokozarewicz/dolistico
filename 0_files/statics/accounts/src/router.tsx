import { Routes, Route } from 'react-router-dom';

import UpdatePassword from './pages/update-password';
import UpdateEmail from './pages/update-email';
import DeleteAccount from './pages/delete-account';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/update-password" element={<UpdatePassword />} />
      <Route path="/update-email" element={<UpdateEmail />} />
      <Route path="/delete-account" element={<DeleteAccount />} />
    </Routes>
  );
}