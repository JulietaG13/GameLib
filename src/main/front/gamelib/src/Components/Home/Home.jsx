import React, {useState} from "react"
import '../LoginSignup/LoginSignup.css'

import email_icon from '../Assets/mail-icon.png'
import password_icon from '../Assets/password icon.png'
import {Link, Navigate} from "react-router-dom";
import axios from "axios";


const Home = () => {
    return(
        <div className='container'>
            <div className='header'>
                <div className="text">{"Home"}</div>
                <div className="underline"></div>
            </div>
            <div className="submit-container">
                <button className={"submit"} type={"submit"}>
                    <Link to="/login" className={"link"}> Login </Link>
                </button>
                <button className={"submit"} type={"submit"} >
                    <Link to="/register" className={"link"}> Register </Link>
                </button>
            </div>
        </div>
    );
};
export default Home;


