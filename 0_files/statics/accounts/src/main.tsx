import React from 'react';
import ReactDOM from 'react-dom/client';
import { AppRoutes } from './router';
import './i18n';
import './main.css'
import { BrowserRouter } from 'react-router-dom';
import config from './config.json';

ReactDOM.createRoot(
    document.getElementById('root')!
).render(
    <React.StrictMode>
        <BrowserRouter basename={config.BASE_PATH}>
            <AppRoutes />
        </BrowserRouter>
    </React.StrictMode>
);