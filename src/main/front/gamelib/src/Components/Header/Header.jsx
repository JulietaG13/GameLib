import React, { useEffect, useRef, useState } from "react";
import gameLibLogoRework from '../Assets/gamelibLogoReRework.png';
import user_icon from "../Assets/user-icon.png";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";

function Header() {
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const navigate = useNavigate();

    function handleLogout() {
        if (!isLoggedIn) {
            window.location.href = '/login';
        }
        localStorage.clear();
        setIsLoggedIn(false);
        window.location.href = '/';
    }

    function handleDeleteUser() {
        axios.post(`http://localhost:4567/deleteuser/${localStorage.getItem('username')}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
            }).catch(error => {
            console.error('Error:', error);
        });
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
        validateLogin();
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
        });
    }

    function handleSearch(event) {
        if (event.key === 'Enter' && searchQuery.trim() !== '') {
            navigate(`/search?query=${searchQuery}`);
        }
    }


    return (
        <div className="flex items-center bg-[#ff8341] h-20 px-4 justify-between">
            <div className="flex items-center">
                <a href="http://localhost:3000/">
                    <img src={gameLibLogoRework} width={80} height={80} className="ml-3 min-w-[50px]" alt="" />
                </a>
                <div
                    className="flex bg-slate-300 p-2 w-full h-12 items-center mx-4 rounded-full max-w-[600px] flex-grow">
                    <input
                        type="text"
                        placeholder="Search Games"
                        className="bg-transparent outline-none w-full"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyDown={handleSearch}
                    />
                </div>
            </div>
            <div className="flex items-center">
                {isLoggedIn ? (
                    <div className="flex flex-row justify-end p-1 items-center relative" ref={dropdownRef}>
                    <h2 className="mr-2 font-helvetica">{localStorage.getItem('username') || 'Name'}</h2>
                        <img src={user_icon} alt="user icon" className="cursor-pointer w-[3em] h-[3em]" onClick={toggleDropdown} />
                        {showDropdown && (
                            <div className="dropdown-content absolute bg-gray-100 w-40 py-2 shadow-md z-10 top-full right-0 flex flex-col pl-2 rounded-s">
                                <Link to={`/profile/${localStorage.getItem("username")}`}>Profile</Link>
                                <a href="#" onClick={handleLogout}>Logout</a>
                                <a onClick={handleDeleteUser}>Delete account</a>
                            </div>
                        )}
                    </div>
                ) : (
                    <h2 className="font-bold pr-14">
                        <a  className={"font-avenir text-[26px]"} href="/login">Login</a>
                    </h2>
                )}
            </div>
        </div>
    );
}

export default Header;
