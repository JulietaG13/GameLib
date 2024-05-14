import gamelib_logo from '../Assets/Designer(3).jpeg'
import React, {useState} from 'react';
import axios from "axios";
import {Link, Navigate} from "react-router-dom";

const LoginRework = () => {
    const [username, setMail] = useState('');
    const [password, setPassword] = useState('');
    const [navigate, setNavigate] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');



    const ErrorMessage = ({ message }) => {
        return (
            <div className={message ? 'formErrorHandling' : ''}>
                {message}
            </div>
        );
    };


    const handleLogin = async () => {
        setErrorMessage('')
        try {
            //sends data to backend
            const response = await axios.post('http://localhost:4567/login', {
                username: username, password: password
            });

            const {token, refreshToken} = response.data;

            localStorage.setItem('token', token);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('username', username);

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


    //if navigate is true, redirects to home page, this happens only when logged successfully
    if (navigate) {
        return <Navigate to={"/library"}/>
    }


    return (
        //Main container
        <div className={"w-full h-screen flex items-start"}>
            {/*Left side*/}
            <div className={"relative w-1/2 h-full flex flex-col"}>
                <img src={gamelib_logo} className={"w-full h-full object-cover brightness-50"}/>
                <div className={"absolute top-[15%] left-[10%] flex flex-col"}>
                    <h1 className={"text-6xl text-white font-bold my-4"}>
                        Welcome to GameLib
                    </h1>
                    <p className={"text-4xl text-white font-normal"}>Join now for free!</p>
                </div>
            </div>

            {/*Right side*/}
            <div className={"w-1/2 h-full flex flex-col p-20 justify-between"}>
                {/*Main Title */}
                <h1 className={"text-3xl text-black font-semibold"}>
                    Incredible title
                </h1>
                {/*Subtitle*/}
                <div className={"w-full flex flex-col max-w-[700px]"}>
                    <h3 className={"text-2xl font-semibold mb-4"}>Login</h3>
                    <p className={"text-base mb-2"}>Welcome back! Please enter your credentials.</p>

                    {/*Email input */}
                    <div className={"w-full flex flex-col"}>
                        <input type="text"
                               placeholder={"Username"}
                               className={"w-full text-black py-4 my-2 border-b border-black bg-transparent outline-none focus:outline-none"}
                               onChange={e => setMail(e.target.value)}
                        />
                        {/*Password input */}
                        <input type="password"
                               placeholder={"Password"}
                               className={"bg-transparent  w-full text-black py-4 my-2 border-b border-black outline-none focus:outline-none"}
                               onChange={e => setPassword(e.target.value)}
                        />


                    </div>
                    <div >
                        <ErrorMessage message={errorMessage} />
                    </div>
                    {/*Login button*/}
                    <div className={"w-full flex flex-col my-4"}>
                        <button
                            className={"w-full text-white bg-black rounded-md my-2 p-4 text-center flex items-center justify-center font-bold"}  onClick={handleLogin}>
                           Login
                        </button>
                    </div>
                </div>


                {/*Bottom*/}
                <div className={"w-full flex items-center justify-center my-2 max-w-[700px]"}>
                    <p className={"text-sm font-normal text-black"}>Don't have an account?
                        <span className={"pl-2 font-bold underline underline-offset-auto cursor-pointer"}>
                            <Link to={"/register"}>Sign up for free</Link>
                        </span>
                    </p>
                </div>
            </div>
        </div>
    )
}

export default LoginRework;