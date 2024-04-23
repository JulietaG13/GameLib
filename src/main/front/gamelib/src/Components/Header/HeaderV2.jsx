import React, {useEffect, useRef, useState} from "react";
import gamelibLogo from '../Assets/gamelib-logo.jpg';
import {HiOutlineMagnifyingGlass} from "react-icons/hi2";
import user_icon from "../Assets/user-icon.png";
import './Header.css';

function HeaderV2() {
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    function handleLogout() {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('username');
        setIsLoggedIn(false);
        window.location.href = '/';
    }

    const toggleDropdown = () => {
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
    }, []);



    useEffect(() => {
        // Check if user is logged in using token
        if (localStorage.getItem('token')) {
            setIsLoggedIn(true);
        } else {
            setIsLoggedIn(false);
        }
    }, []);



    return (
        <div className={'flex items-center mt-1 bg-gray-200'}>
            <a href={'http://localhost:3000/'}>
                <img src={gamelibLogo} width={60} height={60} className={'ml-3'}/>
            </a>
            <div className={'flex bg-slate-300 p-2 w-full items-center ml-10 mr-20 rounded-full'}>
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
                    <img src={user_icon} width={60} height={60} alt={"user icon"} className={'cursor-pointer'}/>
                    {showDropdown && (
                        <div className="dropdown-content">
                            <a href="#">Profile</a>
                            <a href="#" onClick={handleLogout}>Logout</a>
                        </div>
                    )}
                </div>
            ) : (
                <div className="user">
                    <h2>
                        <a href="/login">Login</a>
                    </h2>
                </div>
            )}
        </div>
    )
}

export default HeaderV2;