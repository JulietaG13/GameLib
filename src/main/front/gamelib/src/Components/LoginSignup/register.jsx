import React, {useState} from "react"
import './LoginSignup.css'

import email_icon from '../Assets/mail-icon.png'
import password_icon from '../Assets/password icon.png'
import user_icon from '../Assets/user-icon.png'
import {Link} from "react-router-dom";

const Register = () => {
    //React code, provides a variable and a set to provide the variable data
    return(
        <div className='container'>
            <div className='header'>
                <div className="text">Sign up</div>
                <div className="underline"></div>
            </div>
            <div className="inputs">
                <div className="input">
                    <img src={user_icon} alt=""/>
                    <input type="text" placeholder={"Name"}/>
                </div>
                <div className="input">
                    <img src={email_icon} alt=""/>
                    <input type="email" placeholder={"Email"}/>
                </div>
                <div className="input">
                    <img src={password_icon} alt=""/>
                    <input type="password" placeholder={"Password"}/>
                </div>
            </div>
            <div className="forgot-password">Do you already have an account? <Link to={"/login"} className={"link"}>Log in</Link></div>
            <div className="submit-container">
                <div className={"submit"} onClick={() => {
                }}>Sign up
                </div>
            </div>
        </div>
    );
};
export default Register;


