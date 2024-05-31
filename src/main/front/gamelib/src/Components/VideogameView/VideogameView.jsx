import React, {useEffect, useState} from "react";
import {Navigate, useParams} from "react-router-dom";
import axios from "axios";
import user_icon from "../Assets/user-icon.png";
import pencil_icon from "../Assets/pencil-icon.png";
import './VideogameView.css';
import NewsComp from "./NewsComp";

function VideogameView() {
    const videogameID = useParams();
    const [user, setUser] = useState({});

    const [videogame, setVideogame] = useState({});
    const [reviews, setReviews] = useState([]);
    const [review, setReview] = useState('');

    const [errorMessage, setErrorMessage] = useState('');

    const [navigateHome, setNavigateHome] = useState(false);
    const [navigateEdit, setNavigateEdit] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        axios.get(`http://localhost:4567/getuser/${localStorage.getItem('username')}`)
            .then(response => {
                console.log(response.data);
                setUser(getIDAndRol(response.data));
            })
            .catch(() => {
                setUser(-1);
            })
    }, []);

    useEffect(() => {
        axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
            .then(response => {
                setVideogame(response.data);
                console.log(response.data);
                setIsLoading(false);
            })
            .catch(error => {
                console.error('Error:', error);
                setNavigateHome(true);
            });
    }, [videogameID]);

    useEffect(() => {
        axios.get(`http://localhost:4567/getreviews/${videogameID.videogameID}/2`)
            .then(response => {
                setReviews(response.data);
                // console.log(response.data);
            })
            .catch(error => {
                console.log(error);
            });
    }, [videogameID, review]);

    const ErrorMessage = ({ message }) => {
        return (
            <div className={message ? 'errorMessageDiv' : ''}>
                {message}
            </div>
        );

    }

    const publishReview = (event) => {
        event.preventDefault();
        if (review === '') return;
        axios.post(`http://localhost:4567/newreview/${videogameID.videogameID}`, {
            'text': review
        }, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(r => setReview(''))
            .catch(r => {
                    if (!r.response.status) {
                        setErrorMessage("Something went wrong")
                    } else if (r.response.status === 401) {
                        setErrorMessage("You need to be logged in to post a review.");
                    } else {
                        setErrorMessage(r.response.data)
                    }
                    setReview('');
                    console.error('Error:', r);
                }
            );
    }

    const redirectEdit = () => {
        setNavigateEdit(true);
    }

    if(navigateHome) {return <Navigate to={`/`}/>;}
    if(navigateEdit) {return <Navigate to={`/editVideogame/${videogameID.videogameID}`}/>;}
    if (isLoading) {return loadingScreen();}

    return (
        <main className={"gameView"}>
            <img id={"backImg"} src={videogame.background_image} alt={"Game Background"}/>
            <div className={"titleDiv"}>
                <h1>{videogame.name}</h1>
            </div>

            <div className={"dataDiv"}>
                <div className={"coverDiv"}>
                    <img src={videogame.cover} alt={"Game Cover"}/>
                </div>

                <div className={"moreDataDiv"}>
                    {checkPrivilege(user, videogame.owner_id) ?
                        <div className={'goToEdit'}>
                            <button onClick={redirectEdit}><img alt={"Edit videogame"} src={pencil_icon} /></button>
                        </div>
                        :
                        null
                    }

                    <div className={"attributesDiv"}>
                        <h2>About the game:</h2>
                        <p>{videogame.description}</p>
                        <p>Date of release: {formatDate(videogame.release_date)}</p>
                        {videogame.tags.length === 0 ?
                            <p>No tags available</p>
                            :
                            <p>Tags: {videogame.tags.map((tag) => tag.name + ', ')}</p>
                        }
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

                        <div>
                            <ErrorMessage message={errorMessage}/>
                        </div>

                        {reviews.length === 0 ?
                            <div className={"reviewDiv"}>
                                <img id={"special"} src={user_icon} alt={"user_icon"}/>
                                <p>Be the first one to review!</p>
                            </div>
                            :
                            reviews.reverse().map((review) => (
                                <div key={review.id} className={"reviewDiv"}>
                                    <img src={user_icon} alt={"user_icon"}/>
                                    <p>{review.text}</p>
                                </div>
                            ))
                        }
                    </div>
                </div>

                <div className={"newsDiv"}>
                    <NewsComp videogameID={videogameID.videogameID}/>
                </div>
            </div>
        </main>
    );
}

function getIDAndRol(user) {
    return {
        id: user.id,
        rol: user.rol
    };

}

function checkPrivilege(user, ownerID) {
    if (user.rol === "ADMIN") {
        return true;
    }
    return user.id === ownerID;
}

function formatDate(date) {
    let d = new Date(date);
    return (d.getDate() + 1) + "/" + (d.getMonth() + 1) + "/" + d.getFullYear();
}

function loadingScreen() {
    return (
        <div className={"loadingScreen"}>
            <h1>Loading...</h1>
        </div>
    )
}

export default VideogameView;