import React from 'react';
import ReactDOM from 'react-dom/client';
import { HashRouter } from 'react-router-dom';
import { AppRoutes } from './router';
import './i18n';

ReactDOM.createRoot(
    document.getElementById('root')!
).render(
    <React.StrictMode>
        <HashRouter>
            <AppRoutes />
        </HashRouter>
    </React.StrictMode>
);