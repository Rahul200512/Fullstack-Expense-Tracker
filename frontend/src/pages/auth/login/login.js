import {useEffect, useState, useRef} from 'react';
import '../../../assets/styles/register.css';
import {useForm} from 'react-hook-form';
import { Link, useNavigate} from 'react-router-dom';
import AuthService from '../../../services/auth.service';
import API_BASE_URL from '../../../services/auth.config';
import Logo from '../../../components/utils/Logo';

function Login() {

    const navigate = useNavigate();

    const [serverStatus, setServerStatus] = useState('checking'); // 'checking', 'awake', 'waking'

    useEffect(() => {
        if (AuthService.getCurrentUser() && AuthService.getCurrentUser().roles.includes("ROLE_USER")) {
            navigate("/user/dashboard");
        }else if (AuthService.getCurrentUser() && AuthService.getCurrentUser().roles.includes("ROLE_ADMIN")) {
            navigate("/admin/transactions");
        }
    }, [])

    // Check if backend server is awake
    useEffect(() => {
        let cancelled = false;
        const timeout = setTimeout(() => {
            if (!cancelled && serverStatus === 'checking') setServerStatus('waking');
        }, 3000);

        const checkServer = () => {
            fetch(API_BASE_URL + '/auth/signin', { method: 'POST', mode: 'no-cors' })
                .then(() => { if (!cancelled) { setServerStatus('awake'); clearTimeout(timeout); } })
                .catch(() => {
                    if (!cancelled) {
                        setServerStatus('waking');
                        const interval = setInterval(() => {
                            fetch(API_BASE_URL + '/auth/signin', { method: 'POST', mode: 'no-cors' })
                                .then(() => { if (!cancelled) { setServerStatus('awake'); clearInterval(interval); } })
                                .catch(() => {});
                        }, 5000);
                        setTimeout(() => clearInterval(interval), 120000);
                    }
                });
        };
        checkServer();

        return () => { cancelled = true; clearTimeout(timeout); };
    }, [])


    const {register, handleSubmit,formState} = useForm();

    const [response_error, setResponseError] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [isDemoLoading, setIsDemoLoading] = useState(false);
    const [warmupMsg, setWarmupMsg] = useState("");

    const onSubmit = async (data) => {
        setIsLoading(true)
        setWarmupMsg("");
        const warmupTimer = setTimeout(() => {
            setWarmupMsg("Server is waking up, please wait...");
        }, 3000);
        await AuthService.login_req(data.email, data.password).then(
            () => {
                clearTimeout(warmupTimer);
                setWarmupMsg("");
                setResponseError("");

                setTimeout(() => {
                    if (AuthService.getCurrentUser().roles.includes("ROLE_USER")) {
                        navigate("/user/dashboard");
                    }else if (AuthService.getCurrentUser().roles.includes("ROLE_ADMIN")) {
                        navigate("/admin/transactions");
                    }
                }, 5000)
                localStorage.setItem("message", JSON.stringify({ status: "SUCCESS", text: "Login successfull!" }))
            },
            (error) => {
                clearTimeout(warmupTimer);
                setWarmupMsg("");
                const resMessage =(error.response && error.response.data && error.response.data.message) || error.message || error.toString();
                console.log(resMessage);
                if (resMessage === "Bad credentials"){
                    setResponseError("Invalid email or password!");
                }else {
                    setResponseError("Something went wrong: Try again later!");
                }
            }
          );
        setIsLoading(false);
    }

    const handleDemoLogin = async () => {
        setIsDemoLoading(true);
        setResponseError("");
        setWarmupMsg("");
        const warmupTimer = setTimeout(() => {
            setWarmupMsg("Server is waking up, please wait...");
        }, 3000);
        await AuthService.login_req("demo@mywallet.com", "Demo@1234").then(
            () => {
                clearTimeout(warmupTimer);
                setWarmupMsg("");
                setResponseError("");
                setTimeout(() => {
                    if (AuthService.getCurrentUser().roles.includes("ROLE_USER")) {
                        navigate("/user/dashboard");
                    }
                }, 5000)
                localStorage.setItem("message", JSON.stringify({ status: "SUCCESS", text: "Welcome to Demo!" }))
            },
            (error) => {
                clearTimeout(warmupTimer);
                setWarmupMsg("");
                console.log(error);
                setResponseError("Demo account not available. Please register a new account.");
            }
        );
        setIsDemoLoading(false);
    }

    return(
        <div className='container'>
            <form className="auth-form"  onSubmit={handleSubmit(onSubmit)}>
            <Logo/>
                <h2>Login</h2>

                {serverStatus === 'waking' && (
                    <div style={{
                        background: '#e0f2fe',
                        border: '1px solid #7dd3fc',
                        borderRadius: '8px',
                        padding: '10px 14px',
                        marginBottom: '10px',
                        fontSize: '13px',
                        color: '#0369a1',
                        textAlign: 'center',
                        animation: 'pulse 2s infinite',
                        width: '100%'
                    }}>
                        ⏳ Server is starting up (free hosting). This takes about 30-60 seconds...
                    </div>
                )}

                {warmupMsg && !serverStatus.includes('waking') && (
                    <div style={{
                        background: '#fef9c3',
                        border: '1px solid #fde047',
                        borderRadius: '8px',
                        padding: '10px 14px',
                        marginBottom: '10px',
                        fontSize: '13px',
                        color: '#854d0e',
                        textAlign: 'center',
                        width: '100%'
                    }}>
                        ⏳ {warmupMsg}
                    </div>
                )}

                {
                    (response_error!=="") && <p>{response_error}</p>
                }

                <div className='input-box'>
                    <label>Email</label><br/>
                    <input
                        type='text'
                        {...register('email', {
                            required: "Email is required!",
                            pattern: {value:/^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/g, message:"Invalid email address!"}
                        })}
                    />
                    {formState.errors.email && <small>{formState.errors.email.message}</small>}
                </div>

                <div className='input-box'>
                    <label>Password</label><br/>
                    <input
                        type='password'
                        {
                            ...register('password', {
                                required: 'Password is required!'
                            })
                        }
                    />
                    {formState.errors.password && <small>{formState.errors.password.message}</small>}
                </div>
                <div className='msg'> <Link to={'/auth/forgetpassword/verifyEmail'} className='inline-link'>Forgot password?</Link></div><br/>

                <div className='input-box'>
                    <input type='submit' value={isLoading ? "Logging in..." : 'Login'}
                        className={isLoading ? "button button-fill loading" : "button button-fill"}
                    />
                </div>

                <div className='input-box' style={{marginTop: '0px'}}>
                    <button
                        type='button'
                        onClick={handleDemoLogin}
                        disabled={isDemoLoading}
                        className="button"
                        style={{
                            width: '100%',
                            padding: '8px',
                            border: '2px solid var(--blue)',
                            borderRadius: '5px',
                            backgroundColor: 'transparent',
                            color: 'var(--blue)',
                            fontSize: '14px',
                            fontWeight: '600',
                            cursor: 'pointer',
                            transition: '0.2s'
                        }}
                    >
                        {isDemoLoading ? (warmupMsg ? "⏳ Server starting..." : "Loading Demo...") : "Try Demo"}
                    </button>
                </div>

                <br/><div className='msg'>New member? <Link to='/auth/register' className='inline-link'>Register Here</Link></div>
            </form>
        </div>
    )
}

export default Login;