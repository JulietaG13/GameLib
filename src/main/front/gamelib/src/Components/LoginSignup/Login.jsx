import React, {useState} from "react"
import './LoginSignup.css'

import email_icon from '../Assets/mail-icon.png'
import password_icon from '../Assets/password icon.png'
import user_icon from '../Assets/user-icon.png'
import {Link} from "react-router-dom";


const LoginSignup = () => {
    //React code, provides a variable and a set to provide the variable data
    const [action,setAction] = useState("Sign Up");


    return(
        <div className='container'>
            <div className='header'>
                <div className="text">{"Login"}</div>
                <div className="underline"></div>
            </div>
            <div className="inputs">
                <div className="input">
                    <img src={email_icon} alt=""/>
                    <input type="email" placeholder={"Email"}/>
                </div>
                <div className="input">
                    <img src={password_icon} alt=""/>
                    <input type="password" placeholder={"Password"}/>
                </div>
            </div>
            <div className="forgot-password">New here? <Link to="/register" className={"link"}> Register </Link></div>
            <div className="submit-container">
                <div className={"submit"}
                     onClick={() => {}}>Login
                </div>
            </div>

        </div>
    );
};
export default LoginSignup;


