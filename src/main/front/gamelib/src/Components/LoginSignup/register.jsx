import React, {useState} from "react"
import './LoginSignup.css'

import email_icon from '../Assets/mail-icon.png'
import password_icon from '../Assets/password icon.png'
import user_icon from '../Assets/user-icon.png'
import {Link} from "react-router-dom";
import axios from "axios";
import {Navigate} from "react-router-dom";

export const Register = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [navigate, setNavigate] = useState(false);

    //sends data to backend
    const submit = async e => {
        //prevents page to reload
        e.preventDefault()

        await axios.post("http://localhost:4567/newuser", {
            username: username, email: email, password: password, rol: "USER"
        });

        setNavigate(true);
    }

    if (navigate){
        return <Navigate to={"/login"}/>;
    }

    return <form onSubmit={submit}>
        <main className={"form-signin"}>
            <div className='container'>
                <div className='header'>
                    <div className="text">Sign up</div>
                    <div className="underline"></div>
                </div>
                <div className="inputs">
                    <div className="input">
                        <img src={user_icon} alt=""/>
                        <input type="text" placeholder={"Name"}
                            //saves value
                               onChange={e => setUsername(e.target.value)}
                        />
                    </div>
                    <div className="input">
                        <img src={email_icon} alt=""/>
                        <input type="email" placeholder={"Email"}
                               onChange={e => setEmail(e.target.value)}
                        />
                    </div>
                    <div className="input">
                        <img src={password_icon} alt=""/>
                        <input type="password" placeholder={"Password"}
                               onChange={e => setPassword(e.target.value)}
                        />
                    </div>
                </div>
                <div className="forgot-password">Do you already have an account?
                    <Link to={"/login"} className={"link"}>Log in</Link>
                </div>
                <div className="submit-container">
                    <button className={"submit"} title={"Register"} onClick={submit}/>
                </div>
            </div>
            );
        </main>
    </form>
};


