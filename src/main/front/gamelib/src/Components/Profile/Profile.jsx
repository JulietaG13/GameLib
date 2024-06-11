import React, { useEffect, useState } from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import { useParams, useNavigate, Navigate } from "react-router-dom";
import axios from "axios";
import Header from "../Header/Header";
import Shelves from "./Shelves";
import AlertMessage from "./AlertMessage";

function Profile() {
    const navigate = useNavigate();
    const { username } = useParams();
    const [usernameResponse, setUsernameResponse] = useState('');
    const [description, setDescription] = useState('');
    const [notFound, setNotFound] = useState(false);
    const [isFriend, setIsFriend] = useState(false);
    const [isPending, setIsPending] = useState(false); // State to track if the friend request is pending
    const [isFollowing, setIsFollowing] = useState(false); // State to track follow status
    const [isBanned, setIsBanned] = useState(false); // State to track if the user is banned
    const [profilePicture, setProfilePicture] = useState(userProfile); // State for profile picture
    const [bannerImage, setBannerImage] = useState(gamelib_logo); // State for banner image
    const loggedInUsername = localStorage.getItem('username');
    const [alert, setAlert] = useState({ message: '', visible: false, position: { top: 0, left: 0 } }); // State for alert

    const handleAdminAction = () => {
        axios.put('http://localhost:4567/admin/ban/' + localStorage.getItem("currentProfileId"), {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            console.log("User banned successfully");
            setIsBanned(true); // Update state to indicate the user is banned
        }).catch(e => {
            console.error('Error:', e);
        })
    };

    const validateLogin = async () => {
        try {
            await axios.post('http://localhost:4567/tokenvalidation', {}, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            });
            return true;
        } catch (error) {
            console.error('Error validating login:', error);
            return false;
        }
    };

    const handleUnbanUser = () => {
        axios.put('http://localhost:4567/admin/unban/' + localStorage.getItem("currentProfileId"), {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            console.log("User unbanned successfully");
            setIsBanned(false); // Update state to indicate the user is unbanned
        }).catch(e => {
            console.error('Error:', e);
        })
    };

    const handleFollow = async (event) => {
        const isLoggedIn = await validateLogin();
        if (!isLoggedIn) {
            showAlert("You need to be logged in to perform this action.", event);
            return;
        }

        const endpoint = isFollowing ? 'unsubscribe' : 'subscribe';
        axios.post(`http://localhost:4567/dev/subs/${endpoint}/${localStorage.getItem("currentProfileId")}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                setIsFollowing(!isFollowing); // Toggle the follow status
            })
            .catch(error => {
                console.error(`Error ${isFollowing ? 'unfollowing' : 'following'} developer:`, error);
            });
    };

    const showAlert = (message, event) => {
        const rect = event.target.getBoundingClientRect();
        let leftPosition = rect.left;
        if (leftPosition + 300 > window.innerWidth) {
            leftPosition = window.innerWidth - 320; // Ajuste para que el popup no se salga de la pantalla
        }
        setAlert({
            message,
            visible: true,
            position: { top: rect.top + rect.height - 70, left: leftPosition }
        });
        setTimeout(() => setAlert({ ...alert, visible: false }), 6000);
    };

    useEffect(() => {
        if (loggedInUsername !== username) {
            axios.get(`http://localhost:4567/user/friends/status/${localStorage.getItem("currentProfileId")}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            })
                .then(response => {
                    console.log(response.data);
                    setIsFriend(response.data.is_friend);
                    setIsPending(response.data.is_sent);
                })
                .catch(error => {
                    console.error("Error checking friendship status:", error);
                });
        }
    }, [username, loggedInUsername]);

    useEffect(() => {
        if (loggedInUsername !== username) {
            axios.get(`http://localhost:4567/dev/subs/is/${localStorage.getItem("currentProfileId")}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            })
                .then(response => {
                    console.log(response.data);
                    setIsFollowing(response.data.is_subscribed);
                })
                .catch(error => {
                    console.error("Error checking following status:", error);
                });
        }
    }, [username, loggedInUsername]);

    useEffect(() => {
        axios.get(`http://localhost:4567/getprofile/${username}`)
            .then(response => {
                console.log(response.data);

                setIsBanned(response.data.is_banned);

                localStorage.setItem('currentProfileRol', response.data.rol);
                localStorage.setItem('currentProfileId', response.data.id);
                setUsernameResponse(response.data.username);
                setDescription(response.data.biography);
                setProfilePicture(response.data.pfp || userProfile);
                setBannerImage(response.data.banner || gamelib_logo);
            })
            .catch(() => {
                setNotFound(true);
            });
    }, [username]);

    if (notFound) {
        return <Navigate to="/error" />;
    }

    const navigateToEditProfile = () => {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            navigate(`/profile/${localStorage.getItem("username")}/edit`);
        }).catch(e => {
            console.error('Error:', e);
        });
    };

    const handleAddFriend = async (event) => {
        const isLoggedIn = await validateLogin();
        if (!isLoggedIn) {
            showAlert("You need to be logged in to perform this action.", event);
            return;
        }

        axios.put(`http://localhost:4567/user/friends/send/${localStorage.getItem("currentProfileId")}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                console.log("Friend request sent:", response);
                setIsPending(true);
            })
            .catch(error => {
                console.error("Error adding friend:", error);
            });
    };

    const handleDeleteFriend = async (event) => {
        const isLoggedIn = await validateLogin();
        if (!isLoggedIn) {
            showAlert("You need to be logged in to perform this action.", event);
            return;
        }

        axios.put('http://localhost:4567/user/friends/remove/' + localStorage.getItem("currentProfileId"), {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                setIsFriend(false);
                setIsPending(false);
            })
            .catch(error => {
                console.error("Error removing friend:", error);
            });
    };

    return (
        <div>
            <Header />
            <div className='relative'>
                <div className='flex flex-col md:flex-row'>
                    <div className='flex-grow'>
                        {/* Banner */}
                        <div className='bg-white relative  '>
                            <img src={bannerImage} className="w-full h-[250px] object-cover border-2 border-black" alt="Banner"/>
                            {/* Buttons */}
                            {loggedInUsername === username ? (
                                // Edit Profile button for the user's own profile
                                <button onClick={navigateToEditProfile} className="absolute top-4 right-4 text-white py-2 px-4 rounded bg-black">
                                    Edit Profile
                                </button>
                            ) : (
                                // Add/Remove Friend and Follow/Unfollow buttons for other profiles
                                <div className="absolute top-4 right-4 flex space-x-2">
                                    {isFriend ? (
                                        <button onClick={handleDeleteFriend} className="bg-red-600 text-white py-2 px-4 rounded">
                                            Remove Friend
                                        </button>
                                    ) : (
                                        isPending ? (
                                            <button className="bg-gray-600 text-white py-2 px-4 rounded">
                                                Request Sent
                                            </button>
                                        ) : (
                                            <button onClick={handleAddFriend} className="bg-green-600 text-white py-2 px-4 rounded">
                                                Add Friend
                                            </button>
                                        )
                                    )}

                                    {localStorage.getItem('currentProfileRol') === 'DEVELOPER' && (
                                        <button onClick={handleFollow} className="bg-yellow-500 text-white py-2 px-4 rounded">
                                            {isFollowing ? 'Unfollow' : 'Follow'}
                                        </button>
                                    )}

                                    {/* Admin Action Button (only visible to admins) */}
                                    {localStorage.getItem('rol') === 'ADMIN' && (
                                        <button onClick={isBanned ? handleUnbanUser : handleAdminAction} className="bg-purple-600 text-white py-2 px-4 rounded">
                                            {isBanned ? 'Unban' : 'Ban'}
                                        </button>
                                    )}
                                </div>
                            )}
                            {alert.visible && (
                                <div style={{ position: 'absolute', top: alert.position.top, left: alert.position.left }}>
                                    <AlertMessage message={alert.message} onClose={() => setAlert({ ...alert, visible: false })} />
                                </div>
                            )}
                            {/* Profile Information */}
                            <div className="flex  w-4/5 md:w-3/4 lg:w-1/2 h-auto items-center mx-auto md:mx-16 z-40 -mt-32 rounded-lg p-4">
                                <img src={profilePicture} className="h-52 w-52 md:h-56 md:w-56 bg-gray-400 object-cover rounded-full border-2 border-black" alt="User Profile"/>
                                <div className="ml-4 pt-20 ">
                                    <h1 className="font-bold text-2xl md:text-3xl pt-10  pl-10  ">{usernameResponse}</h1>
                                    <h2 className="font-semibold text-lg md:text-xl pt-2 pb-1 pl-20">About me</h2>
                                    <p className="font-normal pl-20">{description}</p>
                                </div>
                            </div>
                        </div>
                        {/* Shelves */}
                        <div className="bg-white mt-8">
                            <h1 className="pl-4 md:pl-16 font-bold text-4xl pt-5">All Shelves</h1>
                            <Shelves username={username} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Profile;
