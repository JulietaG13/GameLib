import React, { useEffect, useState } from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import { useParams, useNavigate, Navigate, Link } from "react-router-dom";
import axios from "axios";
import HeaderV2 from "../Header/HeaderV2";
import Shelves from "./Shelves";

function Profile() {
    const navigate = useNavigate();
    const { username } = useParams();
    const [usernameResponse, setUsernameResponse] = useState('DefaultName');
    const [description, setDescription] = useState('DefaultDescription');
    const [notFound, setNotFound] = useState(false);
    const [friends, setFriends] = useState([]);
    const [isMenuOpen, setIsMenuOpen] = useState(false);

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
        axios.get(`http://localhost:4567/user/friends/get/${localStorage.getItem( "id")}/${username}`)
            .then(response => {
                setFriends(response.data.friends);
            })
            .catch(error => {
                console.error("Error fetching friends:", error);
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
            }
        ).then(() => {
                navigate(`/profile/${localStorage.getItem("username")}/edit`);
            }
        ).catch(e => {
            console.error('Error:', e);
        });
    }

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    }

    return (
        <div>
            <HeaderV2 />
            <div className='relative'>
                <div className='flex flex-col md:flex-row'>
                    <div className='flex-grow'>
                        {/* Banner */}
                        <div className='bg-white relative'>
                            <img src={gamelib_logo} className="w-full h-[250px] object-cover" alt="GameLib Logo"/>
                            {/* Botón de edición */}
                            {loggedInUsername === username && (
                                <button onClick={navigateToEditProfile}
                                        className="absolute top-4 right-4 bg-blue-600 text-white py-2 px-4 rounded">
                                    Edit Profile
                                </button>
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
                {/* Options Menu Icon */}
                {loggedInUsername === username && (
                    <>
                        <div className={`absolute top-4 ${isMenuOpen ? 'right-32' : 'right-0'} mt-12 transform ${isMenuOpen ? 'translate-x-0' : 'translate-x-1/2'}`}>
                            <button onClick={toggleMenu} className="bg-gray-600 text-white py-2 px-4 rounded-full">
                                ☰
                            </button>
                        </div>
                        {/* Sidebar Menu */}
                        <div className={`fixed top-0 right-0 h-full bg-gray-100 p-4 shadow-lg transition-transform duration-300 ${isMenuOpen ? 'translate-x-0' : 'translate-x-full'}`}>
                            <h2 className='font-bold text-xl mb-4'>Friends</h2>
                            {friends.length === 0 ? (
                                <p>No friends found</p>
                            ) : (
                                <ul>
                                    {friends.map(friend => (
                                        <li key={friend.username} className='mb-2'>
                                            <Link to={`/profile/${friend.username}`} className='text-blue-600 block p-2 hover:bg-gray-100'>
                                                {friend.username}
                                            </Link>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}

export default Profile;
