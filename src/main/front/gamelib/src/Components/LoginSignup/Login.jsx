import React, {useState} from "react"
import './LoginSignup.css'

import email_icon from '../Assets/mail-icon.png'
import password_icon from '../Assets/password icon.png'
    import {Link, Navigate} from "react-router-dom";
import axios from "axios";


const Login = () => {
    //React code, provides a variable and a set to provide the variable data
    const [mail, setMail] = useState('');
    const [password, setPassword] = useState('');
    const [navigate, setNavigate] = useState(false);

    const submit = async e => {
        e.preventDefault();
        const response = await axios.post('http://localhost:8080/api/login', {
            mail: mail, password: password
        }, {withCredentials: true});

        console.log(response.data)


        //setNavigate(true);
    }

    if (navigate) {
        return <Navigate to={"/"}/>
    }

    return(
        <form onSubmit={submit}>
            <div className='container'>
                <div className='header'>
                    <div className="text">{"Login"}</div>
                    <div className="underline"></div>
                </div>
                <div className="inputs">
                    <div className="input">
                        <img src={email_icon} alt=""/>
                        <input type="email" placeholder={"Email"}
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
                <div className="forgot-password">New here? <Link to="/register" className={"link"}> Register </Link></div>
                <div className="submit-container">
                    <div className={"submit"}
                         onClick={() => {}}>Login
                    </div>
                </div>

            </div>
        </form>
    );
};
export default Login;


