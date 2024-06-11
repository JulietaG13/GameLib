import React, { useEffect, useState } from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import pencil_icon from "../Assets/pencil-icon.png"; // Import the overlay image
import { Navigate, useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import Header from "../Header/Header";
import Shelves from "./Shelves";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes } from '@fortawesome/free-solid-svg-icons';

function EditProfile() {
    const { username } = useParams();
    const navigate = useNavigate(); // Hook para redirigir
    const [usernameResponse, setUsernameResponse] = useState('');
    const [description, setDescription] = useState('');
    const [notFound, setNotFound] = useState(false);

    // Save the new data to be changed
    const [newBackgroundImage, setNewBackgroundImage] = useState(null);
    const [newProfilePicture, setNewProfilePicture] = useState(null);
    const [newDescription, setNewDescription] = useState(description);
    const [newUsername, setNewUsername] = useState(username);

    const loggedInUsername = localStorage.getItem('username');

    useEffect(() => {
        axios.get(`http://localhost:4567/getprofile/${username}`)
            .then(response => {
                localStorage.setItem('id', response.data.id);
                setUsernameResponse(response.data.username);
                setDescription(response.data.description);
                setNewBackgroundImage(response.data.banner || gamelib_logo);
                setNewProfilePicture(response.data.pfp || userProfile);
            })
            .catch(() => {
                setNotFound(true);
            });
    }, [username]);

    if (notFound) {
        return <Navigate to="/error" />;
    }

    const handleImageChange = (event, setImage) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setImage(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    function handleSave() {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            axios.post(`http://localhost:4567/user/profile/${username}/edit`, {
                username: newUsername,
                biography: newDescription,
                banner: newBackgroundImage,
                pfp: newProfilePicture
            }, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            }).then((response) => {
                localStorage.setItem('username', newUsername);
                localStorage.setItem('description', newDescription);
                localStorage.setItem('token', response.data.token);
                // Redirects to the new profile
                if (newUsername !== username) {
                    navigate(`/profile/${newUsername}`);
                } else {
                    navigate(`/profile/${username}`);
                }
            }).catch(e => {
                console.error('Error:', e);
            })
        }).catch(e => {
            console.error('Error:', e);
        })
    }

    return (
        /* Main container */
        <div>
            <Header />
            <div className='relative'>
                {/* Botón de cerrar */}
                <Link to={`/profile/${username}`} className="absolute top-10 left-4 bg-gray-600 text-white py-2 px-4 rounded-full z-50">
                    <FontAwesomeIcon icon={faTimes} />
                </Link>
                {/* Banner */}
                <div className='bg-white relative'>
                    <div className="relative md:w-full h-[250px]">
                        <img src={newBackgroundImage} className={"md:w-full h-[250px] object-cover"} alt={""} onClick={() => document.getElementById('banner-upload').click()}/>
                        <input
                            type="file"
                            accept="image/*"
                            style={{ display: 'none' }}
                            id="banner-upload"
                            onChange={(event) => handleImageChange(event, setNewBackgroundImage)}
                        />
                        <img src={pencil_icon} className="absolute inset-0 m-auto h-16 w-16 cursor-pointer" alt="Edit" onClick={() => document.getElementById('banner-upload').click()} />
                    </div>
                    {/* Botón de edición */}
                    {loggedInUsername === username && (
                        <button onClick={handleSave}
                                className="absolute top-4 right-4 bg-blue-600 text-white py-2 px-4 rounded">
                            Save
                        </button>
                    )}
                    {/* Profile Information */}
                    <div
                        className="flex  w-4/5 md:w-3/4 lg:w-1/2 h-auto items-center mx-auto md:mx-16 z-40 -mt-32 rounded-lg p-4">
                        <div className="relative h-52 w-52 md:h-56 md:w-56">
                            <img src={newProfilePicture}
                                 className="h-full w-full bg-gray-400 object-cover rounded-full"
                                 alt="User Profile" onClick={() => document.getElementById('profile-upload').click()}/>
                            <input
                                type="file"
                                accept="image/*"
                                style={{ display: 'none' }}
                                id="profile-upload"
                                onChange={(event) => handleImageChange(event, setNewProfilePicture)}
                            />
                            <img src={pencil_icon} className="absolute inset-0 m-auto h-16 w-16 cursor-pointer" alt="Edit" onClick={() => document.getElementById('profile-upload').click()} />
                        </div>
                        <div className={"pl-4 pt-28"}>
                            <input type={'text'} value={newUsername}
                                   onChange={(event) => setNewUsername(event.target.value)}
                                   className={"flex font-bold items-center text-2xl border-2 border-black rounded-s"}></input>
                            <h2 className={"font-semibold text-xl pt-4 pb-1"}>About me</h2>
                            <input type='text' value={newDescription}
                                   onChange={event => setNewDescription(event.target.value)}
                                   className={"font-normal pl-5 pr-1 border-2 border-black rounded-s"}></input>
                        </div>
                    </div>
                </div>
                {/* Shelves */}
                <div className={"bg-white"}>
                    <h1 className={"pl-16 font-bold text-[35px] pt-5"}>All Shelves</h1>
                    <Shelves username={username}/>
                </div>
            </div>
        </div>
    );
}

export default EditProfile;
