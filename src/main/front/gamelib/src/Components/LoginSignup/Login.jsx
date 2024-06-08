import gamelib_logo from '../Assets/Designer(3).jpeg'
import React, {useEffect, useState} from 'react';
import axios from "axios";
import {Link, Navigate} from "react-router-dom";

const Login = () => {
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
        useEffect(() => {
            validateLogin()
        }, []);

        function validateLogin() {
            // Check if user is logged in using token
            axios.post('http://localhost:4567/tokenvalidation', {}, {
                    headers: {
                        'Content-Type': 'application/json',
                        'token': localStorage.getItem('token')
                    }
                }
            ).then(() => {
                    window.location.href = '/';
                }
            ).catch(e => {
                console.error('Error:', e);
            })
        }

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
        return <Navigate to={"/"}/>
    }


    return (
        //Main container
        <div className={"w-full h-screen flex items-start"}>
            {/*Left side*/}
            <div className={"relative w-1/2 h-full flex flex-col"}>
                <Link to="/">
                    <img src={gamelib_logo} className={"w-full h-full object-cover brightness-50 cursor-pointer"} alt="GameLib Logo" />
                </Link>
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
                <h1 className={"text-3xl text-black font-semi bold"}>
                </h1>
                {/*Subtitle*/}
                <div className={"w-full flex flex-col max-w-[700px]"}>
                    <h3 className={"text-2xl font-semi bold mb-4 text-black"}>Log in</h3>
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

export default Login;