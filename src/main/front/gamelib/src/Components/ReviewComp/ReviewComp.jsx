import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import axios from "axios";
import './ReviewComp.css';
import user_icon from "../Assets/user-icon.png";

function ReviewComp() {
    const videogameID = useParams();
    const [comments, setComments] = useState([]);

    useEffect(() => {
        axios.get(`http://localhost:4567/getreviews/${videogameID.videogameID}/2`)
            .then(response => {
                setComments(response.data);
                console.log(response.data);
            })
            .catch(error => {
                console.log(error);
            });
    }, []);

    console.log(comments);

    return (
        <div className="commentBox" >
            <ul>
                {comments.map((comment) => (
                    <li className="text">
                        <img src={user_icon} alt={"user_icon"}/>
                        <p>{comment.text}</p>
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default ReviewComp;