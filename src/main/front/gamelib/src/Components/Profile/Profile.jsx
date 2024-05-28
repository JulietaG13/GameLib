import React, { useEffect, useState } from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import { Navigate, useParams } from "react-router-dom";
import axios from "axios";
import HeaderV2 from "../Header/HeaderV2";
import Shelves from "./Shelves";

function Profile() {
    const { username } = useParams();
    const [usernameResponse, setUsernameResponse] = useState('DefaultName');
    const [description, setDescription] = useState('DefaultDescription');
    const [notFound, setNotFound] = useState(false);

    const loggedInUsername = localStorage.getItem('username');

    useEffect(() => {
        axios.get(`http://localhost:4567/getprofile/${username}`)
            .then(response => {
                setUsernameResponse(response.data.username);
                setDescription(response.data.description);
                //localStorage.setItem('profilePicture', response.data.profilePicture);
                //localStorage.setItem('user_banner', response.data.user_banner);
            })
            .catch(() => {
                setNotFound(true);
            });
    }, [username]);

    if (notFound) {
        return <Navigate to="/error" />;
    }

    return (
        /* Main container */
        <div>
            <HeaderV2 />
            <div className=''>
                {/* Banner */}
                <div className='bg-white relative'>
                    <img src={gamelib_logo} className={"md:w-full h-[250px] object-cover"} alt={""} />
                    {/* Botón de edición */}
                    {loggedInUsername === username && (
                        <button className="absolute top-4 right-4 bg-blue-600 text-white py-2 px-4 rounded">
                            Edit Profile
                        </button>
                    )}
                    {/* Profile Information */}
                    <div className={"bg-blue-500 w-1/5 flex flex-col justify-between ml-16 z-40 -mt-40"}>
                        <img src={userProfile} className={"h-96 bg-amber-200"} alt={""} />
                        <div className={"pl-4"}>
                            <h1 className={"flex font-bold items-center text-2xl"}>{usernameResponse}</h1>
                            <h2 className={"font-semibold text-xl pt-4 pb-1"}>About me</h2>
                            <p className={"font-normal pl-5 pr-1"}>{description}</p>
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

export default Profile;
