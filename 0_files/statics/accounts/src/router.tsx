import { Routes, Route } from 'react-router-dom';

import UpdatePassword from './pages/update-password/page';
import UpdateEmail from './pages/update-email/page';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/update-password" element={<UpdatePassword />} />
      <Route path="/update-email" element={<UpdateEmail />} />
    </Routes>
  );
}