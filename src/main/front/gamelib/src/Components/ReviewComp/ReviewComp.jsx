import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import axios from "axios";
//import './ReviewComp.css';

function ReviewComp() {
    const videogameID = useParams();
    const [comments, setComments] = useState({});

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
        <h1>{comments.actual}</h1>
    )

}

export default ReviewComp;