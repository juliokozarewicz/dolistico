import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import "./css/page.css";
import { updateEmail } from "../../api/update-email";

function App() {
    const { t } = useTranslation();

    // Title
    useEffect(() => {
        document.title = t("UPDATE_EMAIL_BTN").toLocaleUpperCase();
    }, [t]);

    // Extract token from URL hash
    const token = new URLSearchParams(window.location.search).get("token") ?? "";

    // Form state
    const [pin, setPin] = useState<string[]>(["", "", "", "", "", ""]);
    const [pinError, setPinError] = useState(false);
    const inputsRef = useRef<(HTMLInputElement | null)[]>([]);

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
     * Focus the first empty pin input (or the last one if all filled)
     */
    function focusFirstEmpty() {
        const firstEmptyIndex = pin.findIndex((digit) => digit === "");

        if (firstEmptyIndex !== -1) {
            inputsRef.current[firstEmptyIndex]?.focus();
        } else {
            inputsRef.current[pin.length - 1]?.focus();
        }
    }

    /**
     * Handle pin digit change
     */
    function handlePinChange(index: number, value: string) {
        const digit = value.replace(/\D/g, "").slice(-1);

        const newPin = [...pin];
        newPin[index] = digit;
        setPin(newPin);
        setPinError(false);

        if (digit) {
            const nextEmptyIndex = newPin.findIndex((d) => d === "");

            if (nextEmptyIndex !== -1) {
                inputsRef.current[nextEmptyIndex]?.focus();
            } else {
                inputsRef.current[newPin.length - 1]?.focus();
            }
        }
    }

    /**
     * Handle backspace: clears the whole pin (same behavior as the old vanilla JS version)
     */
    function handlePinKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
        if (e.key === "Backspace") {
            setPin(["", "", "", "", "", ""]);
            inputsRef.current[0]?.focus();
        }
    }

    /**
     * Handle paste on the pin boxes
     */
    function handlePinPaste(e: React.ClipboardEvent<HTMLInputElement>) {
        e.preventDefault();

        const paste = e.clipboardData.getData("text").replace(/\D/g, "").slice(0, 6);

        if (!paste) return;

        const newPin = ["", "", "", "", "", ""];
        paste.split("").forEach((digit, i) => {
            newPin[i] = digit;
        });

        setPin(newPin);
        setPinError(false);
        focusFirstEmpty();
    }

    /**
     * Handle email update submit
     */
    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        setLoading(true);
        setErrors([]);
        setPinError(false);

        try {

            const response = await updateEmail(token, pin.join(""));

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
                setPinError(true);

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
                <div id="formUpdateEmailFrame" style={{ display: isSuccess || loading ? "none" : "flex" }}
        >
                    <form id="updateEmailForm" onSubmit={handleSubmit}>
                        <div id="userIcon">
                            <div id="userIconHead"></div>
                            <div id="userIconBody"></div>
                        </div>

                        <p id="text-info">{t("UPDATE_EMAIL_INFO_TEXT")} </p>

                        {/* Pin input box */}
                        <div id="pinBox" onPaste={handlePinPaste}>
                            {pin.map((digit, index) => (
                                <input
                                    key={index}
                                    ref={(el) => { inputsRef.current[index] = el; }}
                                    type="text"
                                    maxLength={1}
                                    inputMode="numeric"
                                    required
                                    value={digit}
                                    className={pinError ? "input-error" : ""}
                                    onClick={focusFirstEmpty}
                                    onChange={(e) => handlePinChange(index, e.target.value)}
                                    onKeyDown={handlePinKeyDown}
                                />
                            ))}
                        </div>

                        <button id="sendButtom" type="submit">{t("UPDATE_EMAIL_BTN")}</button>

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
                    <p className="updateInfo">{t("UPDATE_EMAIL_INFO_SUCCESS")}</p>
                </>
            )}

        </main>
    );
}

export default App;
