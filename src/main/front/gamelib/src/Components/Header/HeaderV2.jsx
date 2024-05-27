import React, {useEffect, useRef, useState} from "react";
import gameLibLogoRework from '../Assets/gamelibLogoReRework.png';
import user_icon from "../Assets/user-icon.png";
import axios from "axios";
import {Link} from "react-router-dom";

function HeaderV2() {
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    function handleLogout() {
        if (!isLoggedIn) {
            window.location.href = '/login';
        }
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('username');
        setIsLoggedIn(false);
        window.location.href = '/';
    }

    function handleDeleteUser() {
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
            setIsLoggedIn(true);
            }
        ).catch(e => {
            console.error('Error:', e);
            setIsLoggedIn(false);
        })
    }

    return (
        <div className={'flex items-center bg-[#ff8341] h-20'}>
            <a href={'http://localhost:3000/'}>
                <img src={gameLibLogoRework} width={80} height={80} className={'ml-3'} alt={""}/>
            </a>
            <div className={'flex bg-slate-300 p-2 w-full h-12 items-center ml-10 mr-20 rounded-full'}>
                <input type={'text'} placeholder={'Search Games'} className={'bg-transparent outline-none '}/>
            </div>
            <div className={'mr-48'}>
            </div>
            {isLoggedIn ? (
            <div className="flex flex-row justify-end p-1 items-center relative"  ref={dropdownRef}>
                <h2>{localStorage.getItem('username') || 'Name'}</h2>
                <img src={user_icon} alt={"user icon"} className={'cursor-pointer w-[5em] h-[4-em]'}
                     onClick={toggleDropdown}/>
                {showDropdown && (
                        <div className="dropdown-content absolute bg-gray-100 w-40 py-2 shadow-md z-10 top-full right-10 flex flex-col pl-2 rounded-b-xl rounded-t-md">
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