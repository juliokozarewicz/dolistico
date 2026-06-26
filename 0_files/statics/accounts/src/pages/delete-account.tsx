import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { deleteAccount } from "../api/delete-account";
import eyeOpen from "../assets/images/open-eye.svg";
import eyeClosed from "../assets/images/closed-eye.svg";

function App() {
    const { t } = useTranslation();

    // Title
    useEffect(() => {
        document.title = t("DELETE_ACCOUNT_BTN").toLocaleUpperCase();
    }, [t]);

    // Extract token from URL hash
    const token = new URLSearchParams(window.location.search).get("token") ?? "";

    // Form state
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [passwordError, setPasswordError] = useState(false);

    // UI state
    const [loading, setLoading] = useState(true);
    const [isSuccess, setIsSuccess] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");

    // Error handling state
    const [errors, setErrors] = useState<string[]>([]);
    const [removing, setRemoving] = useState<number | null>(null);

    /**
     * Simulates initial loading splash (UI effect only)
     */
    useEffect(() => {
        setLoading(true);

        const timer = setTimeout(() => {
            setLoading(false);
        }, 1000);

        return () => clearTimeout(timer);
    }, []);

    /**
     * Handle body background color
     */
    useEffect(() => {
        if (isSuccess) {
            document.body.classList.add("success-background");
        } else {
            document.body.classList.remove("success-background");
        }

        return () => {
            document.body.classList.remove("success-background");
        };
    }, [isSuccess]);

    /**
     * Handle delete account submit
     */
    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        setLoading(true);
        setErrors([]);
        setPasswordError(false);

        try {

            const response = await deleteAccount(token, password);

            const messageCode =
                response?.messageCode ?? response?.data?.messageCode;

            // If API response is invalid
            if (!messageCode) {
                setErrors(["INTERNAL_SERVER_ERROR"]);
                return;
            }

            // Success state
            setSuccessMessage(messageCode);
            setIsSuccess(true);

        } catch (err: any) {

            const status = err?.response?.status ?? err?.status;
            const data = err?.response?.data ?? err?.data;

            // Rate limiting error
            if (status === 429 || data?.messageCode === "TOO_MANY_REQUESTS") {
                setErrors(["TOO_MANY_REQUESTS"]);
                return;
            }

            // Validation error
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

            // Generic API error
            if (data?.messageCode) {
                setErrors([data.messageCode]);
                return;
            }

            // Fallback error
            setErrors(["INTERNAL_SERVER_ERROR"]);

        } finally {
            setLoading(false);
        }
    }

    /**
     * Error auto-dismiss animation queue
     */
    useEffect(() => {
        if (errors.length === 0) return;

        const interval = setInterval(() => {
            setRemoving(0);

            setTimeout(() => {
                setErrors((prev) => prev.slice(1));
                setRemoving(null);
            }, 500);
        }, 3000);

        return () => clearInterval(interval);
    }, [errors]);

    return (
        <main>
            {/* LOADING OVERLAY */}
            {loading && (
                <div id="loading">
                    <div className="spinner" />
                </div>
            )}

            {/* FORM */}
            {!isSuccess && !loading && (
                <div id="formDeleteAccountFrame" style={{ display: isSuccess || loading ? "none" : "flex" }}
        >
                    <form id="deleteAccountForm" onSubmit={handleSubmit}>
                        <div id="userIcon">
                            <div id="userIconHead"></div>
                            <div id="userIconBody"></div>
                        </div>

                        <p id="text-info">{t("DELETE_ACCOUNT_INFO_TEXT")} </p>

                        {/* Password input box */}
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

                            {/* Toggle password visibility */}
                            <button
                                type="button"
                                id="hidepassword"
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                <img
                                    id="hidepasswordimg"
                                    src={showPassword ? eyeClosed : eyeOpen}
                                />
                            </button>
                        </div>

                        <button id="sendButtom" type="submit">{t("DELETE_ACCOUNT_BTN")}</button>

                    </form>
                </div>
            )}

            {/* ERRORS */}
            <div id="errorFrame">
                {errors.map((err, index) => (
                    <div
                        key={err + index}
                        className={`errortext ${
                            index === 0 && removing !== null
                                ? "fadeOutUp"
                                : "fadeIn"
                        }`}
                        style={{ zIndex: 100 - index }}
                    >
                        {t(err)}
                    </div>
                ))}
            </div>

            {/* SUCCESS STATE */}
            {isSuccess && (
                <>
                    <div id="successicon" style={{ display: "block" }} />
                    <p id="textResponse" style={{ display: "block" }}>
                        {t(successMessage)}
                    </p>
                    <p className="updateInfo">{t("DELETE_ACCOUNT_INFO_SUCCESS")}</p>
                </>
            )}

        </main>
    );
}

export default App;