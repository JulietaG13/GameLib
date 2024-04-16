import React, {useState} from "react"
import './LoginSignup.css'

import password_icon from '../Assets/password icon.png'
import {Link, Navigate} from "react-router-dom";
import axios from "axios";
import user_icon from '../Assets/user-icon.png'


const Login = () => {
    //React code, provides a variable and a set to provide the variable data
    const [username, setMail] = useState('');
    const [password, setPassword] = useState('');
    const [navigate, setNavigate] = useState(false);

    const submit = async e => {
        e.preventDefault();
        try {
            //sends data to backend
            const response = await axios.post('http://localhost:4567/loginuser', {
                username: username, password: password
            } );//{withCredentials: true}
            const { token, refreshToken } = response.data;

            //saves token in local storage
            localStorage.setItem('token', token);
            localStorage.setItem('refreshToken', refreshToken);

            //checks if the token is saved
            console.log(response.data)
        }
        catch (error) {
            console.error(e);
        }

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
                <div className="forgot-password">New here? <Link to="/register" className={"link"}> Register </Link></div>
                <div className="submit-container">
                    <div className={"submit"}
                         onClick={submit}>Login
                    </div>
                </div>

            </div>
        </form>
    );
};
export default Login;


