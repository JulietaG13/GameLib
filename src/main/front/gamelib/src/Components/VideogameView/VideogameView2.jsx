import React, {useEffect, useState} from "react";
import {Navigate, useParams} from "react-router-dom";
import axios from "axios";
import user_icon from "../Assets/user-icon.png";
import './VideogameView2.css';

function VideogameView2() {
    const videogameID = useParams();

    const [videogame, setVideogame] = useState({});
    const [reviews, setReviews] = useState([]);
    const [review, setReview] = useState('');

    const [navigate, setNavigate] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
            .then(response => {
                setVideogame(response.data);
                setIsLoading(false);
            })
            .catch(error => {
                console.error('Error:', error);
                setNavigate(true);
            });
    }, [videogameID]);

    useEffect(() => {
        axios.get(`http://localhost:4567/getreviews/${videogameID.videogameID}/2`)
            .then(response => {
                setReviews(response.data);
                console.log(response.data);
            })
            .catch(error => {
                console.log(error);
            });
    }, [videogameID, review]);

    const publishReview = () => {
        if (review === '') return;
        axios.post(`http://localhost:4567/newreview/${videogameID.videogameID}`, {
            'text': review
        }, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(r => setReview(''));
    }

    if(navigate) {return <Navigate to={`/`}/>;}

    if (isLoading) {return loadingScreen();}

    return (
        <main className={"gameView"} >
            <div className={"titleDiv"} >
                <h1>{videogame.name}</h1>
            </div>

            <div className={"dataDiv"} >
                <div className={"coverDiv"} >
                    <img src={videogame.cover} alt={"Game Cover"} />
                </div>

                <div className={"moreDataDiv"} >
                    <div className={"attributesDiv"} >
                        <h2>About the game:</h2>
                        <p>{videogame.description}</p>
                        <p>Date of release: {FormatDate(videogame.releaseDate)}</p>
                    </div>

                    <div className={"reviewsDiv"}>
                        <h2>Reviews section</h2>
                        <form className={'publishReviewDiv'} onSubmit={publishReview}>
                            <input id={'1'}
                                   type={'text'}
                                   placeholder={'Add your review'}
                                   value={review}
                                   onChange={e =>
                                       setReview(e.target.value)
                                   }
                            />
                            <input id={'2'} type={"button"} value={"Publish"} onClick={publishReview}/>
                        </form>

                        {reviews.length === 0 ?
                            <div className={"reviewDiv"}>
                                <img id={"special"} src={user_icon} alt={"user_icon"}/>
                                <p>Be the first one to review!</p>
                            </div> :
                            reviews.map ((review) => (
                                <div key={review.id} className={"reviewDiv"}>
                                    <img src={user_icon} alt={"user_icon"}/>
                                    <p>{review.text}</p>
                                </div>
                            ))
                        }
                    </div>
                </div>

                <div className={"newsDiv"} >
                    <h2>News:</h2>
                </div>
            </div>
        </main>
    );
}

function loadingScreen() {
    return (
        <div className={"loadingScreen"}>
            <h1>Loading...</h1>
        </div>
    )
}

function FormatDate(date) {
    let d = new Date(date);
    return d.getDate() + "/" + (d.getMonth() + 1) + "/" + d.getFullYear();
}

export default VideogameView2;