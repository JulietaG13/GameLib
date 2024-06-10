import React, { useEffect, useState } from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import { useParams, useNavigate, Navigate } from "react-router-dom";
import axios from "axios";
import Header from "../Header/Header";
import Shelves from "./Shelves";

function Profile() {
    const navigate = useNavigate();
    const { username } = useParams();
    const [usernameResponse, setUsernameResponse] = useState('DefaultName');
    const [description, setDescription] = useState('DefaultDescription');
    const [notFound, setNotFound] = useState(false);
    const [isFriend, setIsFriend] = useState(false);
    const [isFollowing, setIsFollowing] = useState(false); // State to track follow status
    const [isBanned, setIsBanned] = useState(false); // State to track if the user is banned
    const loggedInUsername = localStorage.getItem('username');

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

    const handleFollow = () => {
        // Logic to send follow/unfollow request to the backend
        const endpoint = isFollowing ? 'unfollow' : 'follow';
        axios.post(`http://localhost:4567/dev/subs/subscribe/${localStorage.getItem("currentProfileId")}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                console.log(response.data.message);
                setIsFollowing(!isFollowing); // Toggle the follow status
            })
            .catch(error => {
                console.error("Error following/unfollowing developer:", error);
            });
    };

    useEffect(() => {
        if (loggedInUsername !== username && localStorage.getItem('currentProfileRol') === 'developer') {
            // Check if the logged-in user is already following the developer
            axios.get(`http://localhost:4567/dev/subs/subscribe/${localStorage.getItem("currentProfileId")}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            })
                .then(response => {
                    setIsFollowing(response.data.isFollowing);
                })
                .catch(error => {
                    console.error("Error checking follow status:", error);
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
            })
            .catch(() => {
                setNotFound(true);
            });
    }, [username]);

    useEffect(() => {
        if (loggedInUsername !== username) {
            axios.get('http://localhost:4567/user/friends/get/' + localStorage.getItem("currentProfileId"), {})
                .then(response => {
                    console.log(response.data);
                    setIsFriend(response.data.isFriend);
                })
                .catch(error => {
                    console.error("Error checking friendship status:", error);
                });
        }
    }, [username, loggedInUsername]);

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

    const handleAddFriend = () => {
        console.log("Adding friend:", localStorage.getItem("currentProfileId"));
        axios.put(`http://localhost:4567/user/friends/send/${localStorage.getItem("currentProfileId")}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                console.log("Friend request sent:", response);
                setIsFriend(true);
            })
            .catch(error => {
                console.error("Error adding friend:", error);
            });
    };

    const handleDeleteFriend = () => {
        axios.post('http://localhost:4567/user/friends/remove/' + username, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                setIsFriend(false);
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
                        <div className='bg-white relative pt-1 '>
                            <img src={gamelib_logo} className="w-full h-[250px] object-cover" alt="GameLib Logo"/>
                            {/* Buttons */}
                            {loggedInUsername === username ? (
                                // Edit Profile button for the user's own profile
                                <button onClick={navigateToEditProfile} className="absolute top-4 right-4 text-white py-2 px-4 rounded">
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
                                        <button onClick={handleAddFriend} className="bg-green-600 text-white py-2 px-4 rounded">
                                            Add Friend
                                        </button>
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
                            {/* Profile Information */}
                            <div className="flex  w-4/5 md:w-3/4 lg:w-1/2 h-auto items-center mx-auto md:mx-16 z-40 -mt-32 rounded-lg p-4">
                                <img src={userProfile} className="h-48 w-48 md:h-56 md:w-56 bg-amber-200 object-cover" alt="User Profile"/>
                                <div className="ml-4 pt-20 ">
                                    <h1 className="font-bold text-xl md:text-2xl">{usernameResponse}</h1>
                                    <h2 className="font-semibold text-lg md:text-xl pt-2 pb-1">About me</h2>
                                    <p className="font-normal">{description}</p>
                                </div>
                            </div>
                        </div>
                        {/* Shelves */}
                        <div className="bg-white mt-8">
                            <h1 className="pl-4 md:pl-16 font-bold text-2xl pt-5">All Shelves</h1>
                            <Shelves username={username} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Profile;
