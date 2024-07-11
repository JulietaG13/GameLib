import React, {useEffect, useState} from "react";
import {Link, Navigate, useParams} from "react-router-dom";
import axios from "axios";
import user_icon from "../Assets/user-icon.png";
import pencil_icon from "../Assets/pencil-icon.png";
import mail_icon from "../Assets/mail-icon.png";
import un_mail_icon from "../Assets/un-mail-icon.png";
import './VideogameView.css';
import NewsComp from "./NewsComp";
import ShelfManager from "./ShelfManager";
import Header from "../Header/Header";
import ErrorView from "../ErrorView/ErrorView";
import SkeletonComp from "./skeleton/SkeletonComp";
import SmallerSkeletonComp from "./skeleton/SmallerSkeletonComp";

function VideogameView() {
    const videogameID = useParams();
    const [user, setUser] = useState({});
    const [videogame, setVideogame] = useState({});
    const [subscription, setSubscription] = useState(false);
    const [developer, setDeveloper] = useState('');
    const [reviews, setReviews] = useState([]);
    const [review, setReview] = useState('');

    const [errorMessage, setErrorMessage] = useState('');

    const [navigateHome, setNavigateHome] = useState(false);
    const [navigateEdit, setNavigateEdit] = useState(false);
    const [reviewsRetrieved, setReviewsRetrieved] = useState(false);

    useEffect(() => {
        axios.post(`http://localhost:4567/tokenvalidation`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                setUser(getIDAndRol(response.data));
                axios.get(`http://localhost:4567/game/subs/is/${videogameID.videogameID}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        'token': localStorage.getItem('token')
                    }
                })
                    .then(r => {
                        setSubscription(r.data.is_subscribed);
                    })
                    .catch((e) => {
                        console.log(e.response.data);
                    })
            })
            .catch(() => {
                localStorage.clear();
            })
    }, []);

    useEffect(() => {
        axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
            .then(response => {
                setVideogame(response.data);
                axios.get('http://localhost:4567/user/get/' + response.data.owner_id,{
                    headers: {
                        'Content-Type': 'application/json',
                        'token': localStorage.getItem('token')
                    }
                })
                    .then(response => {
                        console.log(response.data);
                        setDeveloper(response.data.username);
                    })
                    .catch(error => {
                        console.error('Error:', error);
                    });
            })
            .catch(error => {
                console.error('Error:', error);
                setNavigateHome(true);
            });
    }, [videogameID]);

    useEffect(() => {
        axios.get(`http://localhost:4567/getreviews/${videogameID.videogameID}/2`)
            .then(response => {
                console.log(response.data);
                setReviews(response.data.reverse());
                setReviewsRetrieved(true);
            })
            .catch(error => {
                console.log(error);
            });
    }, [videogameID, review]);

    const redirectEdit = () => {
        setNavigateEdit(true);
    }

    const handleSubscription = () => {
        let config = {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        };
        if (subscription) {
            axios.post(`http://localhost:4567/game/subs/unsubscribe/${videogameID.videogameID}`, {}, config)
                .then(() => {
                    setSubscription(false);
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        } else {
            axios.post(`http://localhost:4567/game/subs/subscribe/${videogameID.videogameID}`, {}, config)
                .then(() => {
                    setSubscription(true);
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        }
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
            .then(() => {
                setReview('');
                setErrorMessage('');
            })
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

    if(navigateHome) {return <Navigate to={`/`}/>;}
    if(navigateEdit) {return <Navigate to={`/editVideogame/${videogameID.videogameID}`}/>;}

    return (
        <main className={"gameView"}>
            <Header/>
            <img id={"backImg"} src={videogame.background_image} alt={"Game Background"}/>
            <div className={"titleDiv "}>
                <h1>{Object.keys(videogame).length !== 0 ? videogame.name : "Loading game..."}</h1>
            </div>

            <div className={"dataDiv"}>
                <div className={"coverDiv"}>
                    {Object.keys(videogame).length !== 0 ?
                        <img src={videogame.cover} alt={"Game Cover"}/>
                        :
                        <SkeletonComp/>
                    }
                    {Object.keys(user).length !== 0 && Object.keys(videogame).length !== 0 ?
                        <ShelfManager props={videogame}/>
                        :
                        null
                    }
                </div>

                <div className={"moreDataDiv"}>
                    {Object.keys(videogame).length !== 0 ?
                        <div className={'options'}>
                            {checkPrivilege(user, videogame.owner_id) ?
                                <div className={'optionImage acceptOptionImage'}>
                                    <img alt={"Edit videogame"}
                                         title={"Edit videogame"}
                                         src={pencil_icon}
                                         onClick={redirectEdit}/>
                                </div>
                                :
                                null
                            }
                            {Object.keys(user).length !== 0 ?
                                <div
                                    className={`optionImage ${subscription ? 'rejectOptionImage' : 'acceptOptionImage'}`}>
                                    <img alt={"Sub/Unsub videogame"}
                                         title={subscription ? "Unsubscribe" : "Subscribe"}
                                         src={subscription ? un_mail_icon : mail_icon}
                                         onClick={handleSubscription}
                                    />
                                </div>
                                :
                                null
                            }
                        </div>
                        :
                        null
                    }

                    {Object.keys(videogame).length !== 0 ?
                        <div className={"attributesDiv"}>
                            <h2>About the game:</h2>
                            <p>{videogame.description}</p>
                            <p>Developer: {developer !== '' ?
                                <Link to={`/profile/${developer}`}>
                                    {developer}
                                </Link>
                                :
                                "Unknown"
                            }</p>
                            <p>Date of release: {formatDate(videogame.release_date)}</p>
                            {videogame.tags.length === 0 ?
                                <p>No tags available</p>
                                :
                                <p>Tags: {videogame.tags.map(tag => tag.name).join(', ')}</p>
                            }
                        </div>
                        :
                        <SmallerSkeletonComp/>
                    }

                    {reviewsRetrieved ?
                        <div className={"reviewsDiv"}>
                            <h2 className={"pb-5"}>Reviews section</h2>
                            <form className={'publishReviewDiv'} onSubmit={publishReview}>
                            <textarea id={'1'}
                                      placeholder={'Add your review'}
                                      value={review}
                                      maxLength={200}
                                      onChange={e =>
                                          setReview(e.target.value)
                                      }
                            />
                                <button type={'submit'}>Publish</button>
                            </form>

                            {errorMessage !== '' ?
                                <ErrorView message={errorMessage}/>
                                :
                                null
                            }

                            {reviews.length === 0 ?
                                <div className={"reviewDiv w-full mt-5 rounded-xl"}>
                                    <img id={"special"} src={user_icon} alt={"user_icon"}/>
                                    <p>Be the first one to review!</p>
                                </div>
                                :
                                reviews.map((review) => (
                                    <div key={review.id} className={"reviewDiv mt-5 rounded-xl"}>
                                        <Link className={"mr-2"}
                                              to={'/profile/' + review.author.username}
                                              title={`Visit ${review.author.username} page!`}>
                                            <img src={review.author.pfp !== null ? review.author.pfp : user_icon}
                                                 alt={"user_icon"}/>
                                        </Link>
                                        {/*<img src={review.author.pfp !== null ? review.author.pfp : user_icon} alt={"user_icon"}/>*/}
                                        <p>{review.author.username}<br/>{review.text}</p>
                                    </div>
                                ))
                            }
                        </div>
                        :
                        <SmallerSkeletonComp/>
                    }
                </div>

                <div className={"newsDiv"}>
                    <NewsComp videogameID={videogameID.videogameID} owner={checkPrivilege(user, videogame.owner_id)}/>
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

export default VideogameView;