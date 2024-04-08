import React, {useState} from "react"
import './LoginSignup.css'

import email_icon from '../Assets/mail-icon.png'

import password_icon from '../Assets/password icon.png'

import user_icon from '../Assets/user-icon.png'
import gamelib_logo from '../Assets/gamelib-logo.jpg';
const LoginSignup = () => {

    //React code, provides a variable and a set to provide the variable data
    const [action,setAction] = useState("Sign Up");


    return(
        <div className='container'>
            <div className='gamelibLogo'>
                <img src={gamelib_logo}/>
            </div>
            <div className='header'>
                <div className="text">{action}</div>
                <div className="underline"></div>
            </div>
            <div className="inputs">

                {action === "Login" ? <div></div> :
                    <div className="input">
                        <img src={user_icon} alt=""/>
                        <input type="text" placeholder={"Name"}/>
                    </div>}
                <div className="input">
                    <img src={email_icon} alt=""/>
                    <input type="email" placeholder={"Email"}/>
                </div>
                <div className="input">
                    <img src={password_icon} alt=""/>
                    <input type="password" placeholder={"Password"}/>
                </div>
            </div>
            <div className="forgot-password">Lost Password? <span>Click Here!</span></div>
            <div className="submit-container">
                <div className={action === "Login" ? "submit grey" : "submit"} onClick={() => {
                    setAction("Sign Up")
                }}>Sign up
                </div>
                <div className={action === "Sign Up" ? "submit grey" : "submit"}
                     onClick={() => setAction("Login")}>Login
                </div>
            </div>

        </div>
    );
};
export default LoginSignup;


