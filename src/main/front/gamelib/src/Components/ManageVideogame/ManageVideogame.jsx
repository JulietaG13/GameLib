import React, {useEffect, useState} from "react";
import {Navigate, useParams} from "react-router-dom";
import './ManageVideogame.css';
import axios from "axios";

function ManageVideogame({type}) {
    const videogameID = useParams();
    const [tags, setTags] = useState([]);

    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [release_date, setRelease_date] = useState('');
    const [selected_tags, setSelected_tags] = useState([]);
    const [cover, setCover] = useState('');
    const [background_image, setBackground_image] = useState('');
    const [videogame, setVideogame] = useState({});

    const [errorMessage, setErrorMessage] = useState('');

    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [navigate, setNavigate] = useState(false);
    const [toView, setToView] = useState(false);

    let config = {
        headers: {
            'Content-Type': 'application/json',
            'token': localStorage.getItem('token')
        }
    };

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

                                    setVideogame(gameResponseData.data);
                                    setIsLoading(false);
                                    console.log(gameResponseData.data);
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

    const addVideogame = async e => {
        e.preventDefault()

        let dataToSend = {
            name: name,
            description: description,
            release_date: release_date,
            last_update: formatDate(new Date()),
            tags: selected_tags,
            cover: cover,
            background_image: background_image
        };
        console.log(dataToSend);

        await axios.post("http://localhost:4567/game/create", dataToSend, config)
            .then(() =>
                manageSuccess()
            )
            .catch(error => {
                manageFailure(error);
            });
    }

    const editVideogame = async e => {
        e.preventDefault()

        let dataToSend = {
            name: name ? name : videogame.name,
            description: description ? description : videogame.description,
            releaseDate: release_date ? release_date : videogame.release_date,
            lastUpdate: formatDate(new Date()),
            tags: selected_tags ? selected_tags : videogame.tags,
            cover: cover ? cover : videogame.cover,
            backgroundImage: background_image ? background_image : videogame.background_image,
            ownerId: videogame.owner_id
        };
        console.log(dataToSend);

        await axios.put(`http://localhost:4567/editgame/${videogameID.videogameID}`, dataToSend, config)
            .then(() =>
                manageSuccess()
            )
            .catch(error => {
                manageFailure(error);
            });

        // setToView(true);
    }

    const deleteGame = async () => {
        //console.log(1);
        await axios.delete(`http://localhost:4567/deletegame/${videogameID.videogameID}`, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                console.log("prev del game: ");
                console.log(videogame);
                manageSuccess();
                console.log("post del game:");
                console.log(videogame);
            })
        console.log("one more time: ");
        console.log(videogame);
        setNavigate(true);
    }

    function manageSuccess() {
        setVideogame({});
        // setIsSaving(true);
        setToView(true);
    }

    function manageFailure(error) {
        console.log(error.response)
        if (error.response.status) {
            if (error.response.data === "<html><body><h2>404 Not found</h2></body></html>") {
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

    useEffect(() => {
        console.log("tags have been updated: ");
        console.log(selected_tags);
    }, [selected_tags]);

    if (navigate) {
        return <Navigate to={"/"}/>;
    }
    if (isLoading) {
        return standByScreen("Loading videogame...");

    }
    if (isSaving) {
        return standByScreen("Saving videogame...");
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
                               .then(result => setCover(result))
                               .catch(error => console.error(error));
                       }}
                />
                {Object.keys(videogame).length === 0 ?
                    (cover === '' ? null : <img src={cover} alt={"cover1"}/>) :
                    (videogame.cover === null ? (cover === '' ? null : <img src={cover} alt={"cover2"}/>) :
                        <img src={videogame.cover} alt={"cover3"}/>)
                }
            </div>

            <div className={'cover'}>
                <input type={'File'}
                       accept={'image/*'}
                       onChange={e => {
                           formatBase64Image(e.target.files[0])
                               .then(result => setBackground_image(result))
                               .catch(error => console.error(error));
                       }}
                />
                {Object.keys(videogame).length === 0 ?
                    (background_image === '' ? null : <img src={background_image} alt={"cover1"}/>) :
                    (videogame.background_image === null ? (background_image === '' ? null : <img src={background_image} alt={"cover2"}/>) :
                        <img src={videogame.background_image} alt={"cover3"}/>)
                }
            </div>

            <div className={"titleDesc flex justify-center items-center"}>
                <input className={'p-1 rounded mb-2'}
                       type={"text"}
                       placeholder={"Add videogame name"}
                       defaultValue={videogame.name}
                       onChange={e => setName(e.target.value)}
                />

                <input id={"desc"}
                       type={"text"}
                       className={'p-1 rounded mb-2'}
                       placeholder={"Add description"}
                       defaultValue={videogame.description}
                       onChange={e => setDescription(e.target.value)}
                />
            </div>

            <div className={"tagsSelectionDiv"}>
                <h2>Select tags:</h2>
                <div className={"tagsDiv"}>
                    {tags.map((tag, index) => (
                        <div key={index} className={"tagDiv"}>
                            <input
                                type="checkbox"
                                checked={selected_tags.includes(tag.id)}
                                onChange={() => {
                                    if (selected_tags.includes(tag.id)) {
                                        setSelected_tags(selected_tags.filter(t => t !== tag.id));
                                    } else {
                                        setSelected_tags([...selected_tags, tag.id]);
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
                           defaultValue={videogame.release_date}
                           onChange={e => setRelease_date(e.target.value)}
                    />
                </div>
            </div>

            <div>
                <ErrorMessage message={errorMessage}/>
            </div>

            <div className={"font-bold flex justify-center"}>
                <input type={"button"}
                       className={'submit cursor-pointer mr-2'}
                       value={"Cancel"}
                       onClick={cancel}
                />

                {type === "Edit" ? <input type={"button"}
                                          className={'submit cursor-pointer mr-2'}
                                          value={"Delete"}
                                          onClick={deleteGame}/> : null}

                <input type={"button"}
                       value={type}
                       className={'submit cursor-pointer'}
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

function standByScreen(msg) {
    return (
        <div className={"loadingScreen"}>
            <h1>{msg}</h1>
        </div>
    )
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

function formatAllTagsJSON(tags) {
    return tags.map(tag => formatTagJSON(tag));
}

function formatTagJSON(tag) {
    return {
        id: tag.id,
        name: tag.name,
    }
}

export default ManageVideogame;
