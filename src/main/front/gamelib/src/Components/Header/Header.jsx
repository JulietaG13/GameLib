import React, { useEffect, useRef, useState } from "react";
import gameLibLogoRework from '../Assets/gamelibLogoReRework.png';
import user_icon from "../Assets/user-icon.png";
import bell_icon from '../Assets/notifbell.png';
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import ManagePopup from "../Payment/ManagePopup";
import CheckPopup from "../Popup/CheckPopup";
import userProfile from "../Assets/user-icon.png";
import gamelib_logo from "../Assets/Designer(3).jpeg";

function Header() {
    const [showDropdown, setShowDropdown] = useState(false);
    const [showNotifications, setShowNotifications] = useState(false);
    const [showManagePopup, setShowManagePopup] = useState(false);
    const dropdownRef = useRef(null);
    const notificationsRef = useRef(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [notifications, setNotifications] = useState([]);
    const navigate = useNavigate();
    const [ pfp, setPfp ] = useState(localStorage.getItem('pfp') === "" ? localStorage.getItem('pfp') : user_icon);
    const username = localStorage.getItem('username');
    const isDeveloper = localStorage.getItem('rol') === 'DEVELOPER';
    const [isCheckPopupVisible, setIsCheckPopupVisible] = useState(false);

    function handleLogout() {
        if (!isLoggedIn) {
            window.location.href = '/login';
        }
        localStorage.clear();
        setIsLoggedIn(false);
        window.location.href = '/';
    }

    function handleDeleteUser() {
        axios.post(`http://localhost:4567/user/delete/${localStorage.getItem('id')}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                localStorage.clear()
                window.location.reload();
            }).catch(error => {
            console.error('Error:', error);
        });
    }

    const handleManageDonations = () => {
        setShowManagePopup(true);
    };

    const handleCloseManagePopup = () => {
        setShowManagePopup(false);
    };

    const handleConfirmManagePopup = () => {
        setShowManagePopup(false);
    };

    const toggleDropdown = () => {
        setShowDropdown(!showDropdown);
    };

    const toggleNotifications = () => {
        setShowNotifications(!showNotifications);
    };

    useEffect(() => {
        validateLogin()
        fetchNotifications();
        handleUpdatePfp()
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
            if (notificationsRef.current && !notificationsRef.current.contains(event.target)) {
                setShowNotifications(false);
            }
        }
        // Add event listener when the component mounts
        document.addEventListener("mousedown", handleClickOutside);
        // Remove event listener when the component unmounts
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
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

    function fetchNotifications() {
        // Fetch notifications from backend
        axios.get('http://localhost:4567/notif/get/user/all', {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(response => {
            console.log('Notifications:', response.data.notifications)
            setNotifications(response.data.notifications);
        }).catch(error => {
            console.error('Error fetching notifications:', error);
        });
    }

    function handleSearch(event) {
        if (event.key === 'Enter' && searchQuery.trim() !== '') {
            navigate(`/search?query=${searchQuery}`);
        }
    }

    // checking delete account
    const handleCheckPopupConfirm = () => {
        handleDeleteUser();
        setIsCheckPopupVisible(false);
    };

    const handleCheckPopupCancel = () => {
        setIsCheckPopupVisible(false);
    };

    const showCheckPopup = () => {
        setIsCheckPopupVisible(true);
    };

    const handleUpdatePfp = () => {
        if (username !== '') {
            axios.get(`http://localhost:4567/getprofile/${username}`)
                .then(response => {
                    console.log("handleUpdatePfp: " + response.data);
                    setPfp(response.data.pfp || userProfile);
                })
                .catch((e) => {
                    console.log(e);
                });
        }
    }

    return (
        <div className="flex items-center bg-[#ff8341] h-20 px-4 justify-between">
            <div className="flex items-center">
                <a href="http://localhost:3000/">
                    <img src={gameLibLogoRework} width={80} height={80} className="ml-3 min-w-[50px]" alt="" />
                </a>
                <div
                    className=" flex bg-slate-300 p-2 w-full h-12 items-center mx-4 rounded-full max-w-[600px] flex-grow ml-7 pl-5">
                    <input
                        type="text"
                        placeholder="Search Games"
                        className="bg-transparent outline-none w-full ] "
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyDown={handleSearch}
                    />
                </div>
            </div>
            <div className="flex items-center">
                {isLoggedIn && (
                    <div className="relative" ref={notificationsRef}>
                        <img src={bell_icon} alt="notifications icon" className="cursor-pointer w-6 h-6 mr-6" onClick={toggleNotifications} />
                        {showNotifications && (
                            <div className="dropdown-content absolute bg-gray-100 w-80 py-2 shadow-md z-10 top-full right-0 flex flex-col pl-2 rounded-xl border-2 border-black">
                                <h3 className="font-bold mb-2 text-black">Notifications</h3>
                                {notifications.length === 0 ? (
                                    <p>No notifications found</p>
                                ) : (
                                    <ul>
                                        {notifications.map((notification, index) => (
                                            <a key={index} href={notification.is_game_related ? `http://localhost:3000/videogame/${notification.game_id}` : '#'} onClick={(e) => {
                                                if (!notification.is_game_related) {
                                                    e.preventDefault(); // Prevent default behavior of anchor tag
                                                } else {
                                                    window.location.href = `http://localhost:3000/videogame/${notification.game_id}`;
                                                }
                                            }} className={`block mb-2 relative border-t border-gray-300 pt-2 pb-2 flex items-center`}>
                                                <span className="absolute h-2 w-2 bg-gray-500 rounded-full left-2 top-1/2 transform -translate-y-1/2"></span>
                                                <span className="ml-8 mr-2 flex-grow whitespace-pre-line">{notification.description}</span>
                                            </a>
                                        ))}
                                    </ul>
                                )}
                            </div>
                        )}
                    </div>
                )}
                {isLoggedIn ? (
                    <div className="flex flex-row justify-end p-1 items-center relative mr-8 " ref={dropdownRef}>
                        <h2 className="mr-2 font-helvetica">{localStorage.getItem('username') || 'Name'}</h2>
                        <img src={pfp} className="rounded-full cursor-pointer w-[3em] h-[3em] border-black border-2" onClick={toggleDropdown} />
                        {showDropdown && (
                            <div className="border-2 border-black dropdown-content absolute bg-gray-100 w-40 py-2 shadow-md z-10 top-full right-0 flex flex-col pl-2 rounded-xl">
                                <Link to={`/profile/${localStorage.getItem("username")}`}>Profile</Link>
                                { isDeveloper &&
                                    <a href="#" onClick={handleManageDonations}>Manage Donations</a>
                                }
                                <a href="#" onClick={handleLogout}>Logout</a>
                                <a href="#" onClick={showCheckPopup}>Delete account</a>

                                <ManagePopup
                                    visible={showManagePopup}
                                    onConfirm={handleConfirmManagePopup}
                                    onCancel={handleCloseManagePopup}
                                />
                            </div>
                        )}
                    </div>
                ) : (
                    <h2 className="font-bold pr-14">
                        <a  className={"font-avenir text-[26px]"} href="/login">Login</a>
                    </h2>
                )}
            </div>
            {isCheckPopupVisible && (
                <CheckPopup
                    message="Are you sure you want to delete your account?"
                    onConfirm={handleCheckPopupConfirm}
                    onCancel={handleCheckPopupCancel}
                />
            )}
        </div>
    );
}

export default Header;
