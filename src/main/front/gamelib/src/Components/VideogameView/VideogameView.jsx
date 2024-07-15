import React, {useEffect, useState} from "react";
import {Link, Navigate, useNavigate, useParams} from "react-router-dom";
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
import ReviewsList from "./Reviews/ReviewsList";

function VideogameView() {
    const videogameID = useParams();
    const [user, setUser] = useState({});
    const [videogame, setVideogame] = useState({});
    const [videogameFetched, setVideogameFetched] = useState(false);
    const [subscription, setSubscription] = useState(false);
    const [developer, setDeveloper] = useState('');
    const [reviews, setReviews] = useState([]);
    const [review, setReview] = useState('');

    const [errorMessage, setErrorMessage] = useState('');

    const [navigateHome, setNavigateHome] = useState(false);
    const [reviewsRetrieved, setReviewsRetrieved] = useState(false);

    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

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
                setVideogameFetched(true);
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

    return (
        <main className={"gameView"}>
            <Header/>
            <img id={"backImg"} src={videogame.background_image} alt={"Game Background"}  className={""}/>
            <div className={"titleDiv font-montserrat font-bold"}>
                <h1>{videogameFetched ? videogame.name : "Loading game..."}</h1>
            </div>

            <div className={"dataDiv"}>
                <div className={"coverDiv"}>
                    {videogameFetched ?
                        <img src={videogame.cover} alt={"Game Cover"} className={" rounded-2xl"}/>
                        :
                        <SkeletonComp/>
                    }
                    {Object.keys(user).length !== 0 && videogameFetched ?
                        <ShelfManager props={videogame}/>
                        :
                        null
                    }
                </div>

                <div className={"moreDataDiv"}>
                    {videogameFetched ?
                        <div className={'options'}>
                            {checkPrivilege(user, videogame.owner_id) ?
                                <div className={'optionImage acceptOptionImage'}>
                                    <Link to={`/editVideogame/${videogameID.videogameID}`}>
                                        <img alt={"Edit videogame"}
                                             title={"Edit videogame"}
                                             src={pencil_icon}/>
                                    </Link>
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

                    {videogameFetched ?
                        <div className={"attributesDiv"}>
                            <h2><b>About the game:</b></h2>
                            <div className="description-container">
                                {videogame.description}
                            </div>
                            <div>
                                <p><b>Developer:</b> {developer !== '' ?
                                    <div className={'developer-name'} style={{padding: '2px 10px'}}>
                                        <Link to={`/profile/${developer}`}>
                                            {developer}
                                        </Link>
                                    </div>
                                    :
                                    "Unknown"
                                }</p>
                                <p><b>Date of release:</b> {formatDate(videogame.release_date)}</p>
                            </div>
                            {videogame.tags.filter(t => t.tag_type === "GENRE").length === 0 ?
                                <div>
                                    <p><i>No genres specified</i></p>
                                </div>
                                :
                                <div className="genres-list">
                                    {videogame.tags
                                        .filter(t => t.tag_type === "GENRE")
                                        .map(tag => (
                                            <Link to={`/search?query=${tag.name}`}>
                                                <span className="genre-tag">{tag.name}</span>
                                            </Link>
                                        ))}
                                </div>
                            }
                            {videogame.tags.filter(t => t.tag_type === "PLATFORM").length === 0 ?
                                <div>
                                    <p><i>No platforms specified</i></p>
                                </div>
                                :
                                <div className="genres-list">
                                    <p>
                                        {videogame.tags
                                            .filter(t => t.tag_type === "PLATFORM")
                                            .map(tag => (
                                                <Link to={`/search?query=${tag.name}`}>
                                                    <span className="genre-tag">{tag.name}</span>
                                                </Link>
                                            ))}
                                    </p>
                                </div>
                            }
                            <br/>
                        </div>
                        :
                        <SmallerSkeletonComp/>
                    }

                    {reviewsRetrieved ?
                        <div className={"reviewsDiv"}>
                            <h2 className={"pb-5"} style={{ fontSize: '1.5rem', marginTop: '0.8em' }}>
                                Reviews section
                            </h2>
                            <form className={'publishReviewDiv'} onSubmit={publishReview}>
                            <textarea id={'1'}
                                      placeholder={'Write a review'}
                                      value={review}
                                      maxLength={200}
                                      onChange={e =>
                                          setReview(e.target.value)
                                      }
                                      style={{ margin: '0.1em', marginLeft: '0.2em', padding: '0.5em' }}
                            />
                                <button type={'submit'} style={{ whiteSpace: 'pre' }} className={"ml-4 mr-5"}>
                                    {'  Publish  '}
                                </button>
                            </form>

                            {errorMessage !== '' ?
                                <ErrorView message={errorMessage}/>
                                :
                                null
                            }

                            {reviews.length === 0 ?
                                <div className={"reviewDiv w-full mt-5 rounded-xl"} style={{marginLeft: '0px'}}>
                                    <img id={"special"} src={user_icon} alt={"user_icon"}/>
                                    <p>Be the first one to leave a review!</p>
                                </div>
                                :
                                <ReviewsList reviews={reviews}/>
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