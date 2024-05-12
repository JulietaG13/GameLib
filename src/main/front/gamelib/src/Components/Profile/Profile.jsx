import React, {useEffect, useState} from "react";
import gamelib_logo from "../Assets/Designer(3).jpeg";
import userProfile from "../Assets/user-icon.png";
import {Navigate, useParams} from "react-router-dom";
import axios from "axios";

function Profile(){
    const username = useParams();
    const [navigate, setNavigate] = useState(false);
    const [usernameResponse, setUsernameResponse] = useState('DefaultName');
    const [description, setDescription] = useState('DefaultDescription');


    useEffect(() => {
            axios.get(`http://localhost:4567/getuser/${username.username}`)
                .then(response => {
                    setUsernameResponse(response.data.username);
                    setDescription(response.data.description)
                    //localStorage.setItem('profilePicture', response.data.profilePicture);
                    //localStorage.setItem('user_banner', response.data.user_banner);
                        })
                .catch(() => {
                    //setNavigate(true);
                });

        }
        , []);

    if(navigate) {
        return <Navigate to={"/"}/>;
    }


    return(
        /* Main container */
        <div className=''>
            {/* Banner */}
            <div className='bg-black'>
                <img src={gamelib_logo} className={"md:w-full h-[250px] object-cover"}/>
                {/* Profile Information */}
                <div className={"bg-blue-500 w-1/4 flex flex-col justify-between ml-16 z-40 "}>
                    <img src={userProfile} className={"h-96 -mt-52 bg-amber-200 "}/>
                    <div className={"pl-4"}>
                        <h1 className={"flex font-bold items-center text-2xl"}>{usernameResponse}</h1>
                        <h2 className={"font-semibold text-xl pt-4 pb-1"}>About me</h2>
                        <p className={"font-normal pl-5 pr-1"}>{description}</p>
                    </div>

                </div>
            </div>
        </div>
    )
}
export default Profile;