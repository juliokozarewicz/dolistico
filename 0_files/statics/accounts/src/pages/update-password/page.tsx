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

    // States
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [passwordError, setPasswordError] = useState(false);
    const [loading, setLoading] = useState(false);

    // Errors
    const [errors, setErrors] = useState<string[]>([]);
    const [removing, setRemoving] = useState<number | null>(null);

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

        if (!token) {
            alert("Token inválido");
            return;
        }

        const loadingElement = document.getElementById("loading");
        const formElement = document.getElementById("formUpdatepasswordFrame");

        setLoading(true);

        if (loadingElement) loadingElement.style.display = "flex";
        if (formElement) formElement.style.display = "none";

        try {

            await updatePassword(token, password);
            alert("Senha atualizada com sucesso!");

        } catch (err: any) {

            // Safely extract response data (works with axios, fetch wrappers, interceptors)
            const status = err?.response?.status ?? err?.status;
            const data = err?.response?.data ?? err?.data;

            // 429 - rate limit (primary + fallback by messageCode)
            if (
                status === 429 ||
                data?.messageCode === "TOO_MANY_REQUESTS"
            ) {
                setErrors(["TOO_MANY_REQUESTS"]);
                return;
            }

            // 422 - validation error
            if (status === 422) {
                setPasswordError(true);

                const errorMessages: string[] = [];

                if (data?.messageCode) {
                    errorMessages.push(data.messageCode);
                }

                if (Array.isArray(data?.fieldErrors)) {
                    data.fieldErrors.forEach((fieldError: any) => {
                        if (fieldError.fieldMessageCode) {
                            errorMessages.push(fieldError.fieldMessageCode);
                        }
                    });
                }

                setErrors(errorMessages);
                return;
            }

            // Backend controlled error (translation key)
            if (data?.messageCode) {
                setErrors([data.messageCode]);
                return;
            }

            // Fallback
            setErrors(["INTERNAL_SERVER_ERROR"]);

        } finally {

            setLoading(false);

            if (loadingElement) loadingElement.style.display = "none";
            if (formElement) formElement.style.display = "flex";
        }

    }

    // Fade out error
    useEffect(() => {
        if (errors.length === 0) return;

        const interval = setInterval(() => {
            setRemoving(0);

            setTimeout(() => {
                setErrors(prev => prev.slice(1));
                setRemoving(null);
            }, 500);
        }, 2000); 

        return () => clearInterval(interval);
    }, [errors]);

    return (

        <>

            <main>

                <div id="loading"><div className="spinner"></div></div>

                <div id="formUpdatepasswordFrame">

                    <form id="updatePasswordForm" onSubmit={handleSubmit}>
                    
                        <div id="userIcon">
                            <div id="userIconHead"></div>
                            <div id="userIconBody"></div>
                        </div>

                        <p id="text-info">{t("UPDATE_PASSWORD_INFO_TEXT")}</p>

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

                        <button id="sendButtom" type="submit">{t("UPDATE_PASSWORD_BTN")}</button>

                    </form>

                </div>
                
                <div id="errorFrame">
                    {errors.map((err, index) => (
                        <div
                            key={err + index}
                            className={`errortext ${index === 0 && removing !== null ? "fadeOutUp" : "fadeIn"}`}
                            style={{
                                zIndex: 100 - index
                            }}
                        >
                            {t(err)}
                        </div>
                    ))}
                </div>

                <div id="successicon"></div>
                <p id="textResponse"></p>

            </main>

        </>

    )

}

export default App