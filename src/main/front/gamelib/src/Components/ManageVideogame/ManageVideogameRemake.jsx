import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import './ManageVideogameRemake.css';
import StandByComponent from "./skeleton/StandByComponent";
import ErrorView from "../ErrorView/ErrorView";

function MVR({type}) {
    const videogameID = useParams().videogameID;

    const [videogame, setVideogame] = useState({
        name: '',
        description: '',
        release_date: '',
        last_update: '',
        tags: [],
        cover: '',
        background_image: ''
    });
    const [videogameFetched, setVideogameFetched] = useState(false);
    const [tags, setTags] = useState([]);
    const [tagsFetched, setTagsFetched] = useState(true);

    const [errorMessage, setErrorMessage] = useState('');
    const [disableButton, setDisableButton] = useState(false);

    const navigate = useNavigate();

    // Initial requests to back
    function validateToken() {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .catch(error => {
                console.error('Token validation error:', error);
                localStorage.clear();
                navigate("/");
            })
            .then(validationResponse => {
                // Checks it is no a user that is trying to access the ABM.
                if (isUser(validationResponse)) {
                    navigate("/");
                }

                // Fetches the videogame data to edit
                if (type === "Edit") {
                    fetchGameData(validationResponse);
                } else {
                    setVideogameFetched(true);
                }
            })
    }

    function fetchGameData(validationResponse) {
        axios.get(`http://localhost:4567/getgame/${videogameID}`)
            .catch(error => {
                console.error('Fetching game error:', error);
                navigate("/");
            })
            .then(gameResponse => {
                if (checkNoOwnership(gameResponse, validationResponse)) {
                    navigate("/");
                }

                let formattedJSON = formatJSON(gameResponse.data);
                setVideogame(formattedJSON);
                setVideogameFetched(true);
            })
    }

    function fetchTags() {
        axios.get('http://localhost:4567/tag/get')
            .catch(error => {
                console.error('Fetching tags error:', error);
            })
            .then(tagsResponse => {
                setTags(formatAllTagsJSON(tagsResponse.data));
                setTagsFetched(true);
            })
    }

    useEffect(() => {
        // Checks token is valid
        validateToken();
        fetchTags();
    }, []);

    // Form submitting management
    const manageSubmit = (e) => {
        e.preventDefault()
        setVideogame({...videogame, last_update: formatDate(new Date())});
        setDisableButton(true);

        if (type === "Add") {
            addVideogame();
        } else {
            editVideogame();
        }
    }

    const addVideogame = () => {
        axios.post("http://localhost:4567/game/create", videogame, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                manageSuccess();
            })
            .catch(error => {
                manageFailure(error);
            });
    }

    const editVideogame = () => {
        axios.put(`http://localhost:4567/game/edit/${videogameID}`, videogame, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() =>
                manageSuccess()
            )
            .catch(error => {
                manageFailure(error);
            });
    }

    const manageSuccess = () => {
        setVideogame({
            name: '',
            description: '',
            release_date: '',
            last_update: '',
            tags: [],
            cover: '',
            background_image: ''
        });
        const route = type ==="Add" ? "/" : `/videogame/${videogameID}`;
        navigate(route);
    }

    const manageFailure = (error) => {
        console.log("Submitting videogame error: " + error)
        setDisableButton(false);
        if (error.response.status) {
            if (error.response.data === <html><body><h2>404 Not found</h2></body></html>) {
                setErrorMessage("Something went wrong")
            } else {
                setErrorMessage(error.response.data)
            }
        } else {
            setErrorMessage("Something went wrong")
        }
    }

    // View rendering
    return (videogameFetched ?
            <form className={"abmMain"}
                  onSubmit={manageSubmit}
            >
                <div className={"videogameStrings"}>
                    <h1 id={"formTitle"}>{type} videogame</h1>

                    <div className={"titleDivABM divBackground"}>
                        <h2 id={"formSubtitle"}>Title</h2>
                        <input className={"textInput"}
                               type={"text"}
                               defaultValue={videogame.name}
                               onChange={e =>
                                   setVideogame({...videogame, name: e.target.value})
                               }
                        />
                    </div>

                    <div className={"descDivABM divBackground"}>
                        <h2 id={"formSubtitle"}>Description (Optional)</h2>
                        <textarea className={"textInput"}
                                  maxLength={200}
                                  rows={3}
                                  defaultValue={videogame.description}
                                  onChange={e =>
                                      setVideogame({...videogame, description: e.target.value})
                                  }
                        ></textarea>
                    </div>

                    <div className={"tagsDivABM divBackground"}>
                        <h2 id={"formSubtitle"}>Tags (Optional)</h2>
                        {tagsFetched ?
                            <p>several tags</p>
                            :
                            <p>Loading tags...</p>
                        }
                    </div>

                    <div className={"dateDivABM divBackground"}>
                        <h2 id={"formSubtitle"}>Release date</h2>
                        <input className={"dateInput"}
                               type={"date"}
                               defaultValue={videogame.release_date}
                               onChange={e =>
                                   setVideogame({...videogame, release_date: e.target.value})
                               }
                        />
                    </div>

                    {errorMessage !== "" ?
                        <ErrorView message={errorMessage}/>
                        :
                        null
                    }

                    <div className={"buttonsDivABM divBackground"}>
                        <button disabled={disableButton}
                                className={"submitButton"}
                                id={disableButton ? "disabled" : "submit"}
                        >{type}</button>

                        <button disabled={disableButton}
                                className={"submitButton"}
                                id={disableButton ? "disabled" : "cancel"}
                        >Cancel</button>

                        {type === "Edit" ?
                            <button disabled={disableButton}
                                    className={"submitButton"}
                                    id={disableButton ? "disabled" : "delete"}
                            >Delete</button>
                            :
                            null
                        }
                    </div>
                </div>
                <div className={"videogameImages"}>
                    <div className={"imageDivABM divBackground"}>
                        <h2 id={"formSubtitle"}>Cover (Optional)</h2>
                        <input className={"imageInput"}
                               type={"file"}
                               accept={"image/*"}
                               onChange={e => {
                                   formatBase64Image(e.target.files[0])
                                       .then(result => setVideogame({...videogame, cover: result}))
                                       .catch(error => console.error(error));
                               }}
                        />
                        {videogame.cover ?
                            <img className={"imagePreviewABM"}
                                 src={videogame.cover}
                                 alt={"Cover"}
                            />
                            :
                            <p>No cover selected</p>
                        }
                    </div>

                    <div className={"imageDivABM divBackground"}>
                        <h2 id={"formSubtitle"}>Cover (Optional)</h2>
                        <input className={"imageInput"}
                               type={"file"}
                               accept={"image/*"}
                               onChange={e => {
                                   formatBase64Image(e.target.files[0])
                                       .then(result => setVideogame({...videogame, background_image: result}))
                                       .catch(error => console.error(error));
                               }}
                        />
                        {videogame.background_image ?
                            <img className={"imagePreviewABM"}
                                 src={videogame.background_image}
                                 alt={"Background image"}
                            />
                            :
                            <p>No cover selected</p>
                        }
                    </div>
                </div>
            </form>
            :
            <div className={"standByContainer"}>
                <h1>Fetching data...</h1>
                <StandByComponent/>
            </div>
    );
}

function isUser(validationResponse) {
    return !validationResponse || !validationResponse.data || validationResponse.data.rol === "USER"
}

function checkNoOwnership(gameResponseData, userResponseData) {
    if (!gameResponseData || !userResponseData) {
        return true;
    }
    if (!gameResponseData.data || !userResponseData.data) {
        return true;
    }
    if (userResponseData.data.rol === "ADMIN") {
        return false;
    }
    if (gameResponseData.data.owner_id !== userResponseData.data.id) {
        return true;
    }
}

function formatJSON(videogame) {
    return {
        name: videogame.name.toString(),
        description: videogame.description.toString(),
        release_date: videogame.release_date.toString(),
        tags: formatTagsToID(videogame.tags),
        cover: videogame.cover.toString(),
        background_image: videogame.background_image.toString()
    }
}

function formatTagsToID(tags) {
    return tags.map(tag => tag.id);
}

function formatAllTagsJSON(tags) {
    return tags.map(tag => formatTagJSON(tag));
}

function formatTagJSON(tag) {
    return {
        id: tag.id,
        name: tag.name,
    }
}

function formatDate(date) {
    return date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0');
}

function formatBase64Image(image) {
    return new Promise((resolve, reject) => {
        let reader = new FileReader();
        reader.readAsDataURL(image);
        reader.onload = function() {
            resolve(reader.result);
        };
        reader.onerror = function(error) {
            reject(error);
        };
    });
}

export default MVR;
