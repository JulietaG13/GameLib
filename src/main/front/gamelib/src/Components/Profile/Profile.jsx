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

    const loggedInUsername = localStorage.getItem('username');

    useEffect(() => {
        axios.get(`http://localhost:4567/getprofile/${username}`)
            .then(response => {
                localStorage.setItem('id', response.data.id);
                setUsernameResponse(response.data.username);
                setDescription(response.data.biography);
            })
            .catch(() => {
                setNotFound(true);
            });
    }, [username]);

    useEffect(() => {
        if (loggedInUsername !== username) {
            axios.post('http://localhost:4567/user/isFriend', { username })
                .then(response => {
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
            }
        ).then(() => {
                navigate(`/profile/${localStorage.getItem("username")}/edit`);
            }
        ).catch(e => {
            console.error('Error:', e);
        });
    };

    const handleAddFriend = () => {
        console.log("Adding friend:", localStorage.getItem("id"));
        axios.put(`http://localhost:4567/user/friends/send/${localStorage.getItem("id")}`, {},{
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
        axios.post('http://localhost:4567/user/removeFriend', { username })
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
                        <div className='bg-white relative'>
                            <img src={gamelib_logo} className="w-full h-[250px] object-cover" alt="GameLib Logo"/>
                            {/* Add/Remove Friend*/}
                            {loggedInUsername === username ? (
                                <button onClick={navigateToEditProfile}
                                        className="absolute top-4 right-4 bg-blue-600 text-white py-2 px-4 rounded">
                                    Edit Profile
                                </button>
                            ) : (
                                isFriend ? (
                                    <button onClick={handleDeleteFriend}
                                            className="absolute top-4 right-4 bg-red-600 text-white py-2 px-4 rounded">
                                        Remove Friend
                                    </button>
                                ) : (
                                    <button onClick={handleAddFriend}
                                            className="absolute top-4 right-4 bg-green-600 text-white py-2 px-4 rounded">
                                        Add Friend
                                    </button>
                                )
                            )}
                            {/* Profile Information */}
                            <div
                                className="bg-blue-500 w-3/5 md:w-1/3 lg:w-2/12 flex flex-col h-3/5 items-center mx-auto md:mx-16 z-40 -mt-32  rounded-lg">
                                <img src={userProfile}
                                     className="h-48 w-full md:h-56 md:w-full bg-amber-200 object-cover"
                                     alt="User Profile"/>
                                <div className="text-center mt-4">
                                    <h1 className="font-bold text-xl md:text-2xl">{usernameResponse}</h1>
                                    <h2 className="font-semibold text-lg md:text-xl pt-2 pb-1">About me</h2>
                                    <p className="font-normal px-2 md:px-4">{description}</p>
                                </div>
                            </div>
                        </div>
                        {/* Shelves */}
                        <div className="bg-white mt-8">
                            <h1 className="pl-4 md:pl-16 font-bold text-2xl pt-5">All Shelves</h1>
                            <Shelves username={username}/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Profile;
