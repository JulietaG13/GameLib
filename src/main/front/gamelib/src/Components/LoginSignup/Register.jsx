import gamelib_logo from '../Assets/Designer(3).jpeg'
import React, { useEffect, useState } from 'react';
import axios from "axios";
import { Link, Navigate } from "react-router-dom";

const RegisterRework = () => {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [navigate, setNavigate] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [isDeveloper, setIsDeveloper] = useState(false);

    const handleDeveloperCheckboxChange = (event) => {
        setIsDeveloper(event.target.checked);
    };

    const ErrorMessage = ({ message }) => {
        return (
            <div className={message ? 'formErrorHandling' : ''}>
                {message}
            </div>
        );
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await axios.post("http://localhost:4567/newuser", {
                username: username, email: email, password: password, rol: isDeveloper ? "DEVELOPER" : "USER"
            });
            setNavigate(true);
        } catch (error) {
            if (error.response.status) {
                setErrorMessage(error.response.data);
            } else {
                setErrorMessage("Something went wrong");
            }
            console.error('Error:', error);
        }
    };

    useEffect(() => {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            window.location.href = '/';
        }).catch(e => {
            console.error('Error:', e);
        });
    }, []);

    if (navigate) {
        return <Navigate to={"/login"} />;
    }

    return (
        <div className={"w-full h-screen flex items-start"}>
            {/*Left side*/}
            <div className={"relative h-full flex flex-col border-2 border-black"}>
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
                <h1 className={"text-3xl text-black font-semibold"}>
                </h1>
                {/*Subtitle*/}
                <div className={"w-full flex flex-col max-w-[700px]"}>
                    <h3 className={"text-2xl font-semibold mb-4 text-black"}>Register</h3>
                    <p className={"text-base mb-2"}>Welcome!</p>

                    <div className={"w-full flex flex-col"}>
                        {/*Email input */}
                        <input type="text"
                               placeholder={"Email"}
                               className={"w-full text-black py-4 my-2 border-b border-black bg-transparent outline-none focus:outline-none"}
                               onChange={e => setEmail(e.target.value)}
                        />
                        <input type="text"
                               placeholder={"Username"}
                               className={"w-full text-black py-4 my-2 border-b border-black bg-transparent outline-none focus:outline-none"}
                               onChange={e => setUsername(e.target.value)}
                        />
                        {/*Password input */}
                        <input type="password"
                               placeholder={"Password"}
                               className={"bg-transparent w-full text-black py-4 my-2 border-b border-black outline-none focus:outline-none"}
                               onChange={e => setPassword(e.target.value)}
                        />
                    </div>
                    {/*Is developer checkbox */}
                    <div>
                        <input type="checkbox" id="developer" name="developer" value="developer" onChange={handleDeveloperCheckboxChange} />
                        <label htmlFor="developer" className={"text-black"}>Are you a developer?</label>
                    </div>

                    <div>
                        <ErrorMessage message={errorMessage} />
                    </div>
                    {/*Register button*/}
                    <div className={"w-full flex flex-col my-4"}>
                        <button
                            className={"w-full text-white bg-black rounded-md my-2 p-4 text-center flex items-center justify-center font-bold"} onClick={handleRegister}>
                            Register
                        </button>
                    </div>
                </div>

                {/*Bottom*/}
                <div className={"w-full flex items-center justify-center my-2 max-w-[700px]"}>
                    <p className={"text-sm font-normal text-black"}>Already have an account?
                        <span className={"pl-2 font-bold underline underline-offset-auto cursor-pointer"}>
                            <Link to={"/login"}>Log in now!</Link>
                        </span>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default RegisterRework;
