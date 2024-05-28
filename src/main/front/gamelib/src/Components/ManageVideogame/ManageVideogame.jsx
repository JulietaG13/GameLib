import React, {useEffect, useState} from "react";
import './ManageVideogame.css';
import axios from "axios";
import {Navigate, useParams} from "react-router-dom";

function ManageVideogame({type}) {
    const videogameID = useParams();
    const [tags, setTags] = useState([]);

    const [cover, setCover] = useState('');
    const [backgroundImage, setBackgroundImage] = useState('');
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [releaseDate, setReleaseDate] = useState('');
    const [selectedTags, setSelectedTags] = useState([]);
    const [videogame, setVideogame] = useState({});

    const [errorMessage, setErrorMessage] = useState('');

    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [navigate, setNavigate] = useState(false);

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
                axios.get('http://localhost:4567/tags')
                    .catch(error => {
                        console.error('Error en tags:', error);
                        setNavigate(true);
                    })
                    .then(response => {
                        setTags(formatAllTagsJSON(response.data));
                        console.log(formatAllTagsJSON(response.data));
                    })
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
                } else {
                    setIsLoading(false);
                }
            });
    }, []);

    const ErrorMessage = ({ message }) => {
        return (
            <div className={message ? 'formErrorHandling' : ''}>
                {message}
            </div>
        );

    }

    // function checkProperties() {
    //     if (cover.isEmpty()) {
    //         return "Cover is required";
    //     }
    //     if (backgroundImage.isEmpty()) {
    //         return "Background Image is required";
    //     }
    // }

    const addVideogame = async e => {
        e.preventDefault()

        let dataToSend = {
            name: name,
            description: description,
            releaseDate: releaseDate,
            lastUpdate: formatDate(new Date()),
            cover: cover,
            backgroundImage: backgroundImage,
            tags: selectedTags
        };

        // const error = checkProperties(dataToSend);



        console.log(dataToSend)

        await axios.post("http://localhost:4567/newgame", dataToSend, config)
            .then(r =>
                manageSuccess()
            )
            .catch(error => {
                manageFailure(error);
            });
        // setNavigate(true);
    }

    const editVideogame = async e => {
        e.preventDefault()

        let dataToSend = {
            name: name ? name : videogame.name,
            description: description ? description : videogame.description,
            // selectedTags: selectedTags,
            releaseDate: releaseDate ? releaseDate : videogame.releaseDate,
            lastUpdate: formatDate(new Date()),
            cover: cover ? cover : videogame.cover
        };
        await axios.put(`http://localhost:4567/editgame/${videogameID.videogameID}`, dataToSend, config)
            .then(r => manageSuccess())
            .catch(error => {
                console.log(error.response)
                if (error.response.status) {
                    setErrorMessage(error.response.data)
                }
                else {
                    setErrorMessage("Something went wrong")
                }
                console.error('Error:', error);
            });

        setNavigate(true);
    }

    const deleteGame = async () => {
        //console.log(1);
        await axios.delete(`http://localhost:4567/deletegame/${videogameID.videogameID}`, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
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
        setIsSaving(true);
        setNavigate(true);
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
        setNavigate(true);
    }

    // useEffect(() => {
    //     console.log("videogame has been updated: ");
    //     console.log(videogame);
    // }, [videogame]);

    // useEffect(() => {
    //     console.log("cover has been updated!")
    //     console.log(cover)
    //     let sizeInBytes = (cover.length * 3/4);
    //     let sizeInKilobytes = sizeInBytes / 1024;
    //     console.log("Size of cover in Kilobytes: ", sizeInKilobytes);
    // }, [cover]);

    useEffect(() => {
        console.log("tags have been updated: ");
        console.log(selectedTags);
    }, [selectedTags]);

    if(navigate) {
        return <Navigate to={"/"}/>;
    }
    if (isLoading) {
        return standByScreen("Loading videogame...");

    }
    if (isSaving) {
        return standByScreen("Saving videogame...");
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
                               .then(result => setBackgroundImage(result))
                               .catch(error => console.error(error));
                       }}
                />
                {Object.keys(videogame).length === 0 ?
                    (backgroundImage === '' ? null : <img src={backgroundImage} alt={"cover1"}/>) :
                    (videogame.backgroundImage === null ? (backgroundImage === '' ? null : <img src={backgroundImage} alt={"cover2"}/>) :
                        <img src={videogame.backgroundImage} alt={"cover3"}/>)
                }
            </div>

            <div className={"titleDesc flex justify-center items-center"}>
                <input className={'p-1 rounded mb-2'}
                       type={"text"}
                       placeholder={"Add title"}
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
                                checked={selectedTags.includes(tag)}
                                onChange={() => {
                                    if (selectedTags.includes(tag)) {
                                        setSelectedTags(selectedTags.filter(t => t !== tag));
                                    } else {
                                        setSelectedTags([...selectedTags, tag]);
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
                           name={"releaseDate"}
                           defaultValue={videogame.releaseDate}
                           onChange={e => setReleaseDate(e.target.value)}
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
    if (!gameResponseData || !gameResponseData.data || !userResponseData || !userResponseData.data) {
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
        tag_type: tag.tag_type,
        tagged_games: []
    }
}

export default ManageVideogame;
