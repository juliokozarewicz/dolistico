import { useEffect, useState } from "react";
import { useTranslation } from 'react-i18next';
import './css/page.css';

function App() {

    // locale
    const { t } = useTranslation();

    // ======================================================== ( states init )
    // ========================================================= ( states end )

    // ======================================================== ( efects init )

    // Initial loading
    useEffect(() => {
        const loading = document.getElementById("loading");
        const form = document.getElementById("formUpdatepasswordFrame");

        if (!loading || !form) return;
        
        loading.style.display = "flex";
        form.style.display = "none";
        
        const timer = setTimeout(() => {
            loading.style.display = "none";
            form.style.display = "flex";
        }, 1000);
        
        return () => clearTimeout(timer);
    }, []);

    // ========================================================= ( efects end )

    return (

        <>

            <main>

                <div id="loading"><div className="spinner"></div></div>

                <div id="formUpdatepasswordFrame">

                    <form id="updatePasswordForm" method="post">

                        <div id="userIcon">
                            <div id="userIconHead"></div>
                            <div id="userIconBody"></div>
                        </div>
                        
                        <div id="passwordBox">
                            <div id="passIcon"></div>

                            <input
                                type="password"
                                id="password"
                                name="password"
                                placeholder="*************"
                                required
                            />
                        </div>
                        
                        <div id="errorFrame"></div>

                        <button id="sendButtom">{t('UPDATE_PASSWORD')}</button>

                    </form>

                </div>

                <div id="successicon"></div>
                <p id="textResponse"></p>
                
            </main>

        </>

    )

}

export default App