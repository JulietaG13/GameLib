import React, { useEffect, useState } from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import { Navigate, useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import Header from "../Header/Header";
import Shelves from "./Shelves";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes } from '@fortawesome/free-solid-svg-icons';

function EditProfile() {
    const { username } = useParams();
    const navigate = useNavigate(); // Hook para redirigir
    const [usernameResponse, setUsernameResponse] = useState('DefaultName');
    const [description, setDescription] = useState('DefaultDescription');
    const [notFound, setNotFound] = useState(false);

    //save the new data to be changed
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
            })
            .catch(() => {
                setNotFound(true);
            });
    }, [username]);

    if (notFound) {
        return <Navigate to="/error" />;
    }

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
                //banner: newBackgroundImage,
                //pfp: newProfilePicture
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
                    <img src={gamelib_logo} className={"md:w-full h-[250px] object-cover"} alt={""} />
                    {/* Botón de edición */}
                    {loggedInUsername === username && (
                        <button onClick={handleSave} className="absolute top-4 right-4 bg-blue-600 text-white py-2 px-4 rounded">
                            Save
                        </button>
                    )}
                    {/* Profile Information */}
                    <div className={"bg-blue-500 w-1/5 flex flex-col justify-between ml-16 z-40 -mt-40"}>
                        <img src={userProfile} className={"h-96 bg-amber-200"} alt={""} />
                        <div className={"pl-4"}>
                            <input type={'text'} value={newUsername} onChange={(event) => setNewUsername(event.target.value)} className={"flex font-bold items-center text-2xl"}></input>
                            <h2 className={"font-semibold text-xl pt-4 pb-1"}>About me</h2>
                            <input type='text' value={newDescription} onChange={event => setNewDescription(event.target.value)} className={"font-normal pl-5 pr-1"}></input>
                        </div>
                    </div>
                </div>
                {/* Shelves */}
                <div className={"bg-white"}>
                    <h1 className={"pl-16 font-bold text-[35px] pt-5"}>All Shelves</h1>
                    <Shelves username={username} />
                </div>
            </div>
        </div>
    );
}

export default EditProfile;
