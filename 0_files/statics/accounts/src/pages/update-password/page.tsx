import { useEffect, useState } from "react";
import { useTranslation } from 'react-i18next';
import './css/page.css';
import { updatePassword } from "../../api/update-password";
import eyeOpen from "../../assets/images/open-eye.svg";
import eyeClosed from "../../assets/images/closed-eye.svg";

function App() {

    // locale
    const { t } = useTranslation();

    // Retrive token from URL
    const token = new URLSearchParams(window.location.hash.split("?")[1]).get("token");

    // Sstates
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [passwordError, setPasswordError] = useState(false);

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

    // Submit form
    async function handleSubmit(e: React.FormEvent) {

        e.preventDefault();

        console.log(token)

        if (!token) {
            alert("Token inválido");
            return;
        }

        try {

            await updatePassword(token, password);
            alert("Senha atualizada com sucesso!");

        } catch (err) {
            
            // Input error
            if (err.response?.status === 422) {
                setPasswordError(true);
                return;
            }

            console.error(err);
            alert("Erro ao atualizar senha:" + err);
        }

    }

    return (

        <>

            <main>

                <div id="loading">
                    <div className="spinner"></div>
                </div>

                <div id="formUpdatepasswordFrame">
                    <form id="updatePasswordForm" onSubmit={handleSubmit}>
                    
                    <div id="userIcon">
                        <div id="userIconHead"></div>
                        <div id="userIconBody"></div>
                    </div>

                    <div id="passwordBox">

                        <div id="passIcon"></div>

                        <input
                        type={showPassword ? "text" : "password"}
                        id="password"
                        name="password"
                        placeholder="*************"
                        required
                        value={password}
                        className={passwordError ? "passworderror" : ""}
                        onChange={(e) => {
                            setPassword(e.target.value);
                            setPasswordError(false);
                        }}
                        />

                        <button
                            type="button"
                            id="hidepassword"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            <img id="hidepasswordimg" src={showPassword ? eyeClosed : eyeOpen} />
                        </button>

                    </div>

                    <div id="errorFrame"></div>

                    <button id="sendButtom" type="submit">
                        {t("UPDATE_PASSWORD")}
                    </button>

                    </form>
                </div>

                <div id="successicon"></div>
                <p id="textResponse"></p>

            </main>

        </>

    )

}

export default App