import { Routes, Route } from 'react-router-dom';

import UpdatePassword from './pages/update-password/page';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/update-password" element={<UpdatePassword />} />
    </Routes>
  );
}