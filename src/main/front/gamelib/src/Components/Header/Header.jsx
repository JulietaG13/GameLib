// noinspection JSValidateTypes

import React, {useEffect, useRef, useState} from "react";
import './Header.css';

import gamelibLogo from '../Assets/gamelib-logo.jpg';
import user_icon from '../Assets/user-icon.png';
import {Link} from "react-router-dom";

//Header component
function Header() {
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
        <div className="viewsContainer">
            <div className="logo">
                <img src={gamelibLogo} alt={"GameLib logo"}/>
                <h2><Link to="/" className={"link"}> GameLib </Link></h2>
            </div>
            <div className="goTo">
                <h2><a href="https://youtu.be/dQw4w9WgXcQ?si=ZImQDCkmnZI0wC_z">The Library</a></h2>
            </div>
            <div className="goTo">
                <h2><a href="https://youtu.be/dQw4w9WgXcQ?si=ZImQDCkmnZI0wC_z">My GameShelf</a></h2>
            </div>
            <div className="goTo">
                <h2><a href="https://youtu.be/dQw4w9WgXcQ?si=ZImQDCkmnZI0wC_z">Discover</a></h2>
            </div>
            {isLoggedIn ? (
            <div className="user" onClick={toggleDropdown} ref={dropdownRef}>
                <h2>{localStorage.getItem('username') || 'Name'}</h2>
                <img src={user_icon} alt={"user icon"}/>
                {showDropdown  && (
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
    );
}



export default Header;