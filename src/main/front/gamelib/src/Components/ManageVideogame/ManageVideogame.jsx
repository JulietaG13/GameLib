import React, {useEffect, useState} from "react";
import './ManageVideogame.css';
import axios from "axios";
import {Navigate, useParams} from "react-router-dom";

function ManageVideogame({type}) {
    const videogameID = useParams();
    const [gamePicture, setGamePicture] = useState('');
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [releaseDate, setReleaseDate] = useState('');
    const [navigate, setNavigate] = useState(false);
    const [videogame, setVideogame] = useState({});
    const [errorMessage, setErrorMessage] = useState('');

    let item = localStorage.getItem('token');
    let config = {
        headers: {
            'Content-Type': 'application/json',
            'token': item
        }
    };

    useEffect(() => {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': item
            }
        })
            .catch(error => {
                console.error('Error:', error);
                setNavigate(true);
            })
            .then(response => {
                if (type === "Edit") {
                    axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
                        .then(response => {
                            setVideogame(response.data);
                            //console.log(response.data);
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            setNavigate(true);
                        });
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

    const addVideogame = async e => {
        e.preventDefault()

        let dataToSend = {
            name: name,
            description: description,
            releaseDate: releaseDate,
            lastUpdate: FormatLastUpdateDate(new Date()),
            gamePicture: gamePicture
        };
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
            releaseDate: releaseDate ? releaseDate : videogame.releaseDate,
            lastUpdate: FormatLastUpdateDate(new Date()),
            gamePicture: gamePicture ? gamePicture : videogame.gamePicture
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
                'token': item
            }
        })
            .then(response => {
                // console.log("delete response: ");
                // console.log(response.data)
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
        setNavigate(true);
    }

    function manageFailure(error) {
        console.log(error.response)
        if (error.response.status) {
            setErrorMessage(error.response.data)
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

    useEffect(() => {
        console.log("gamePicture has been updated!")
        console.log(gamePicture)
        let sizeInBytes = (gamePicture.length * 3/4);
        let sizeInKilobytes = sizeInBytes / 1024;
        console.log("Size of gamePicture in Kilobytes: ", sizeInKilobytes);
    }, [gamePicture]);

    if(navigate) {
        return <Navigate to={"/"}/>;
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
                           FormatBase64Image(e.target.files[0])
                               .then(result => setGamePicture(result))
                               .catch(error => console.error(error));
                       }}
                />
                {/*{gamePicture === '' ? null : <img src={gamePicture} alt={"gamePicture"}/>}*/}
                {/*<img src={videogame === null ? gamePicture : videogame.gamePicture} alt={"gamePicture"}/>*/}
                {Object.keys(videogame).length === 0 ?
                    (gamePicture === '' ? null : <img src={gamePicture} alt={"gamePicture"}/>) :
                    (videogame.gamePicture === null ? (gamePicture === '' ? null : <img src={gamePicture} alt={"gamePicture"}/>) :
                        <img src={videogame.gamePicture} alt={"gamePicture"}/>)
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

            <div className={"releaseDate font-bold flex justify-start items-center mb-2"}>
                <div className={'flex justify-center'}>
                    <input type={"datetime-local"}
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

function FormatBase64Image(image) {
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

function FormatLastUpdateDate(lastUpdate) {
    let formatedDate = lastUpdate.getFullYear() + '-' +
        String(lastUpdate.getMonth() + 1).padStart(2, '0') + '-' +
        String(lastUpdate.getDate()).padStart(2, '0') + 'T' +
        String(lastUpdate.getHours()).padStart(2, '0') + ':' +
        String(lastUpdate.getMinutes()).padStart(2, '0') + ':' +
        String(lastUpdate.getSeconds()).padStart(2, '0');
    console.log(formatedDate);
    return formatedDate;
}

export default ManageVideogame;