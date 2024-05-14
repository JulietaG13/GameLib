import React, {useEffect, useRef, useState} from "react";
import gamelibLogo from '../Assets/gamelib-logo.jpg';
import {HiOutlineMagnifyingGlass} from "react-icons/hi2";
import user_icon from "../Assets/user-icon.png";
import './Header.css';
import axios from "axios";
import {Link} from "react-router-dom";

function HeaderV2() {
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    function handleLogout() {
        validateLogin()
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('username');
        setIsLoggedIn(false);
        window.location.href = '/library';
    }

    function handleDeleteUser() {
        validateLogin()
        axios.post(`http://localhost:4567/deleteuser/${localStorage.getItem('username')}` , {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
                }
            })
        .then(response => {
            console.log(response);
            localStorage.removeItem('token');
            localStorage.removeItem('username');
        }) .catch (error =>  {
            console.error('Error:', error);
    })}

    let toggleDropdown;
    toggleDropdown = () => {
        setShowDropdown(!showDropdown);
    };

    useEffect(() => {
            function handleClickOutside(event) {
                if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                    setShowDropdown(false);
                }
            }
            // Add event listener when the component mounts
            document.addEventListener("mousedown", handleClickOutside);
            // Remove event listener when the component unmounts
            return () => {
                document.removeEventListener("mousedown", handleClickOutside);
            };
        },
        []);



    useEffect(() => {
        // Check if user is logged in using token
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
                }
            }
        ).then(() => {
            setIsLoggedIn(true);
            }
        ).catch(e => {
            console.error('Error:', e);
            setIsLoggedIn(false);
        })
    }, []);

    function validateLogin() {
        // Check if user is logged in using token
        axios.post('http://localhost:4567/tokenvalidation', {}, {}
        ).then(() => {
            setIsLoggedIn(true);
            }
        ).catch(e => {
            console.error('Error:', e);
            setIsLoggedIn(false);
            window.location.href = '/login';
        })
    }

    return (
        <div className={'flex items-center bg-[#ff8341] h-20'}>
            <a href={'http://localhost:3000/'}>
                <img src={gamelibLogo} width={60} height={60} className={'ml-3'} alt={""}/>
            </a>
            <div className={'flex bg-slate-300 p-2 w-full h-12 items-center ml-10 mr-20 rounded-full'}>
                <HiOutlineMagnifyingGlass/>
                <input type={'text'} placeholder={'Search Games'} className={'bg-transparent outline-none '}/>
            </div>
            <div className={'mr-48'}>
                <h2 className={'font-bold text-black'}>
                    <a href={'http://localhost:3000/library'} className={'text-[25px]'}>Library</a>
                </h2>
            </div>
            {isLoggedIn ? (
                <div className="user" onClick={toggleDropdown} ref={dropdownRef}>
                    <h2>{localStorage.getItem('username') || 'Name'}</h2>
                    <img src={user_icon} alt={"user icon"} className={'cursor-pointer'}/>
                    {showDropdown && (
                        <div className="dropdown-content">
                            <Link to={`/profile/${localStorage.getItem("username")}`}>Profile</Link>
                            <a href="#" onClick={handleLogout}>Logout</a>
                            <a onClick={handleDeleteUser}>Delete account</a>
                        </div>
                    )}

                </div>

            ) : (
                <h2 className={"font-bold mr-10 text-[20px]"}>
                    <a href="/login">Login</a>
                </h2>
            )}
        </div>
    )
}

export default HeaderV2;