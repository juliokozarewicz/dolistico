import { Routes, Route } from 'react-router-dom';

import PageOne from './pages/page-one/page';
import PageTwo from './pages/page-two/page';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/page-one" element={<PageOne />} />
      <Route path="/page-two" element={<PageTwo />} />
    </Routes>
  );
}