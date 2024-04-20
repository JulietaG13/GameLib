import React, {useState} from "react"
import './LoginSignup.css'

import password_icon from '../Assets/password icon.png'
import {Link, Navigate} from "react-router-dom";
import axios from "axios";
import user_icon from '../Assets/user-icon.png'
import Header from "../Header/Header";



const Login = () => {
    //React code, provides a variable and a set to provide the variable data
    const [username, setMail] = useState('');
    const [password, setPassword] = useState('');
    const [navigate, setNavigate] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const handleLogin = async e => {
        setErrorMessage('')
        try {
            //sends data to backend
            const response = await axios.post('http://localhost:4567/login', {
                username: username, password: password
            });

            const {token, refreshToken} = response.data;

            //saves token in local storage
            localStorage.setItem('token', token);
            localStorage.setItem('refreshToken', refreshToken);

            setNavigate(true);

        }
        catch (error) {
            console.log(error.response)
            if (error.response.status) {
                setErrorMessage(error.response.data)
            }
            else {
                setErrorMessage("Something went wrong")
            }
            console.error('Error:', error);
        }
    }

    //Displays error message if there is one
    const ErrorMessage = ({ message }) => {
        return (
            <div className={message ? 'formErrorHandling' : ''}>
                {message}
            </div>
        );
    };

    //if navigate is true, redirects to home page, this happens only when logged successfully
    if (navigate) {
        return <Navigate to={"/"}/>
    }

    return(
        <form className={"static-form"} onSubmit={handleLogin}>
            <div className='container'>
                <div className='header'>
                    <div className="text">{"Login"}</div>
                    <div className="underline"></div>
                </div>
                <div className="inputs">
                    <div className="input">
                        <img src={user_icon} alt=""/>
                        <input type="text" placeholder={"User"}
                        onChange={e => setMail(e.target.value)}

                        />
                    </div>
                    <div className="input">
                        <img src={password_icon} alt=""/>
                        <input type="password" placeholder={"Password"}
                        onChange={e => setPassword(e.target.value)}

                        />
                    </div>
                </div>
                <div >
                    <ErrorMessage message={errorMessage} />
                </div>
                <div className="new-here">New here? <Link to="/register" className={"link"}> Register </Link></div>
                <div className="submit-container">
                    <div className={"submit"}
                         onClick={handleLogin}>Login
                    </div>
                </div>

            </div>
        </form>
    );
};
export default Login;


