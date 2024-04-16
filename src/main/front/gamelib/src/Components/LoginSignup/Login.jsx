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

    const handleLogin = async e => {
        //e.preventDefault();
        try {
            //sends data to backend
            const response = await axios.post('http://localhost:4567/login', {
                username: username, password: password
            });//{withCredentials: true}

            console.log(response.data)

            if (response.status === 200){
                const {token, refreshToken} = response.data;

                //saves token in local storage
                localStorage.setItem('token', token);
                //localStorage.setItem('refreshToken', refreshToken);

                //checks if the token is saved
                //console.log(response.data)
            } else {
                console.log("Error while login") //TODO: show error message
            }
        }
        catch (error) {
            console.error('Error:', error);
        }

        //setNavigate(true);
    }


    const fetchData = async () => {
        try {
            const token = localStorage.getItem('token');

            const response = await axios.get('/protected/resource', {
                headers: {
                    'Token': `${token}`
                }
            });
            if (response.status === 200) {
                const data = response.data;
                // Process the fetched data
            } else {
                // Handle unauthorized access or other errors
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };


    if (navigate) {
        return <Navigate to={"/"}/>
    }

    return(
        <form onSubmit={handleLogin}>
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
                         onClick={handleLogin}>Login
                    </div>
                </div>

            </div>
        </form>
    );
};
export default Login;


