import React, {useEffect, useState} from "react";
import {Navigate, useParams} from "react-router-dom";
import './ManageVideogame.css';
import axios from "axios";
import ErrorView from "../ErrorView/ErrorView";

function ManageVideogame({type}) {
    const videogameID = useParams();
    const [tags, setTags] = useState([]);

    const [theVideogame, setTheVideogame] = useState({
        name: '',
        description: '',
        release_date: '',
        last_update: '',
        tags: [],
        cover: '',
        background_image: ''
    });

    const [errorMessage, setErrorMessage] = useState('');

    const [disableButton, setDisableButton] = useState(false);

    const [isLoading, setIsLoading] = useState(true);
    const [navigate, setNavigate] = useState(false);
    const [toView, setToView] = useState(false);

    useEffect(() => {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .catch(error => {
                console.error('Error en tokenValidation:', error);
                localStorage.clear();
                setNavigate(true);
            })
            .then(userResponseData => {
                checkIfUser(userResponseData, setNavigate);
                axios.get('http://localhost:4567/tag/get')
                    .catch(error => {
                        console.error('Error en tags:', error);
                        setNavigate(true);
                    })
                    .then(response => {
                        setTags(formatAllTagsJSON(response.data));
                        console.log(formatAllTagsJSON(response.data));
                        if (type === "Add") {
                            setIsLoading(false);
                        }
                        if (type === "Edit") {
                            axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
                                .then(gameResponseData => {
                                    checkOwnership(gameResponseData, userResponseData, setNavigate);

                                    let formattedJSON = formatJSON(gameResponseData.data);
                                    setTheVideogame(formattedJSON);
                                    setIsLoading(false);
                                    console.log(formattedJSON);
                                })
                                .catch(error => {
                                    console.error('Error en getGame:', error);
                                    setNavigate(true);
                                });
                        }
                    })

            });
    }, []);

    const ErrorMessage = ({ message }) => {
        return (
            <div className={message ? 'formErrorHandling' : ''}>
                {message}
            </div>
        );

    }

    const addVideogame = e => {
        e.preventDefault()
        setDisableButton(true);
        setTheVideogame({...theVideogame, last_update: formatDate(new Date())});

        axios.post("http://localhost:4567/game/create", theVideogame, {
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

    const editVideogame = e => {
        e.preventDefault()
        setDisableButton(true);
        setTheVideogame({...theVideogame, last_update: formatDate(new Date())});

        axios.put(`http://localhost:4567/game/edit/${videogameID.videogameID}`, theVideogame, {
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

    const deleteGame = () => {
        console.log("About to delete game");
        setDisableButton(true);
        axios.post(`http://localhost:4567/game/delete/${videogameID.videogameID}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                console.log("Game deleted");
                manageSuccess();
                setNavigate(true);
            })
            .catch((e) => {
                console.log("Error deleting game");
                console.log(e);
                manageFailure(e);
            })
    }

    function manageSuccess() {
        setDisableButton(false);
        setTheVideogame({
            name: '',
            description: '',
            release_date: '',
            last_update: '',
            tags: [],
            cover: '',
            background_image: ''
        });
        // setIsSaving(true);
        setToView(true);
    }

    function manageFailure(error) {
        setDisableButton(false);
        console.log(error.response)
        if (error.response.status) {
            if (error.response.data === <html><body><h2>404 Not found</h2></body></html>) {
                setErrorMessage("Something went wrong")
            } else {
                setErrorMessage(error.response.data)
            }
        } else {
            setErrorMessage("Something went wrong")
        }
        console.error('Error:', error);
    }

    const cancel = () => {
        if (type === "Edit") {
            setToView(true);
        } else if (type === "Add") {
            setNavigate(true);
        }
    }

    if (navigate) {
        return <Navigate to={"/"}/>;
    }
    if (isLoading) {
        return standByScreen("Loading videogame...");

    }
    if (toView) {
        return <Navigate to={`/videogame/${videogameID.videogameID}`}/>;
    }

    return (
        <form className={"mainPopUP flex flex-col items-center"}
              onSubmit={type === "Edit" ? editVideogame : addVideogame}
              style={{width: "50%", justifyContent: 'center'}}>
            <h1 className={'font-bold text-[30px] mb-2 text-center'}>{type} Videogame</h1>

            <div className={'cover'}>
                <input type={'File'}
                       accept={'image/*'}
                       onChange={e => {
                           formatBase64Image(e.target.files[0])
                               .then(result => setTheVideogame({...theVideogame, cover: result}))
                               .catch(error => console.error(error));
                       }}
                />

                {
                    theVideogame.cover === '' ?
                        null
                        :
                        <img src={theVideogame.cover} alt={"cover1"}/>
                }
            </div>

            <div className={'cover'}>
                <input type={'File'}
                       accept={'image/*'}
                       onChange={e => {
                           formatBase64Image(e.target.files[0])
                               .then(result => setTheVideogame({...theVideogame, background_image: result}))
                               .catch(error => console.error(error));
                       }}
                />

                {
                    theVideogame.background_image === '' ?
                        null
                        :
                        <img src={theVideogame.background_image} alt={"cover1"}/>
                }
            </div>

            <div className={"titleDesc flex justify-center items-center"}>
                <input className={'p-1 rounded mb-2'}
                       type={"text"}
                       placeholder={"Add videogame name"}
                       defaultValue={theVideogame.name}
                       onChange={e => setTheVideogame({...theVideogame, name: e.target.value})}
                />

                <input id={"desc"}
                       type={"text"}
                       className={'p-1 rounded mb-2'}
                       placeholder={"Add description"}
                       defaultValue={theVideogame.description}
                       onChange={e => setTheVideogame({...theVideogame, description: e.target.value})}
                />
            </div>

            <div className={"tagsSelectionDiv"}>
                <h2>Select tags:</h2>
                <div className={"tagsDiv"}>
                    {tags.map((tag, index) => (
                        <div key={index} className={"tagDiv"}>
                            <input
                                type="checkbox"
                                checked={theVideogame.tags.includes(tag.id)}
                                onChange={() => {
                                    if (theVideogame.tags.includes(tag.id)) {
                                        setTheVideogame({
                                            ...theVideogame,
                                            tags: theVideogame.tags.filter(t => t !== tag.id)
                                        });
                                    } else {
                                        setTheVideogame({
                                            ...theVideogame,
                                            tags: [...theVideogame.tags, tag.id]
                                        });
                                    }
                                }}
                            />
                            <label className={"flex items-center"}>{tag.name}</label>
                        </div>
                    ))}
                </div>
            </div>

            <div className={"releaseDate font-bold flex justify-start items-center mb-2"}>
                <div className={'flex justify-center'}>
                    <input type={"date"}
                           className={'rounded-b'}
                           name={"release_date"}
                           defaultValue={theVideogame.release_date}
                           onChange={e =>
                               setTheVideogame({...theVideogame, release_date: e.target.value})
                           }
                    />
                </div>
            </div>

            {errorMessage !== '' ?
                <ErrorView message={errorMessage}/>
                :
                null
            }

            <div className={"font-bold flex justify-center"}>
                <input type={"button"}
                       disabled={disableButton}
                       className={`${disableButton ? 'disabled' : 'submit'} cursor-pointer mr-2`}
                       value={"Cancel"}
                       onClick={cancel}
                />

                {type === "Edit" ? <input type={"button"}
                                          disabled={disableButton}
                                          className={`cursor-pointer ${disableButton ? 'disabled' : 'submit'}`}
                                          value={"Delete"}
                                          onClick={deleteGame}/> : null}

                <input type={"button"}
                       value={type}
                       disabled={disableButton}
                       className={`${disableButton ? 'disabled' : 'submit'} ml-2 cursor-pointer`}
                       onClick={type === "Edit" ? editVideogame : addVideogame}
                />
            </div>
        </form>
    );
}

function checkIfUser(userResponseData, setNavigate) {
    if (!userResponseData || !userResponseData.data || userResponseData.data.rol === "USER") {
        setNavigate(true);
    }
}

function checkOwnership(gameResponseData, userResponseData, setNavigate) {
    if (!gameResponseData || !userResponseData) {
        setNavigate(true);
    }
    if (!gameResponseData.data || !userResponseData.data) {
        setNavigate(true);
    }
    if (userResponseData.data.rol === "ADMIN") {
        return;
    }
    if (gameResponseData.data.owner_id !== userResponseData.data.id) {
        setNavigate(true);
    }
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

function formatDate(date) {
    return date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0');
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

function standByScreen(msg) {
    return (
        <div className={"loadingScreen"}>
            <h1>{msg}</h1>
        </div>
    )
}

export default ManageVideogame;
