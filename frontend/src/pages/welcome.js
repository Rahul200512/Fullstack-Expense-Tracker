import { Link } from 'react-router-dom';
import '../assets/styles/welcome.css';
import Logo from '../components/utils/Logo';

function Welcome() {
    return (
        <section className="hero-section">
            <Logo/>
            <h2>Welcome to MyWallet!</h2>
            <h3>Meet financial freedom with MyWallet. The application designed to revolutionize how you manage your expenses and empower your financial journey.</h3>

            <div>
                <Link to='/auth/login'><p><button>Log in</button></p></Link>
                <Link to='/auth/register'><button>Create Account</button></Link>
            </div>

            <p style={{
                marginTop: '24px',
                fontSize: '13px',
                opacity: 0.65,
                textAlign: 'right'
            }}>
                Built by <a
                    href="https://github.com/Rahul200512"
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{ color: 'inherit', fontWeight: 600 }}
                >Rahul Reddy Avula</a>
            </p>
        </section>
    )
}

export default Welcome;