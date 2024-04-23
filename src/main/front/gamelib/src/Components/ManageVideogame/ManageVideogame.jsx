import React, {useEffect, useState} from "react";
import './ManageVideogame.css';
import axios from "axios";
import {Navigate, useParams} from "react-router-dom";

function ManageVideogame({type}) {
    const videogameID = useParams();
    // const [gameCapture, setImage] = useState(null);
    // console.log(gameCapture);
    //TODO: Add image upload
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [releaseDate, setReleaseDate] = useState('');
    const [navigate, setNavigate] = useState(false);
    const [videogame, setVideogame] = useState({});
    let item = localStorage.getItem('token');
    console.log('token: ' + item);

    useEffect(() => {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': item
            }
        })
        if(type === "Edit") {
            axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
                .then(response => {
                    setVideogame(response.data);
                    console.log(response.data);
                })
        }
    }, [])

    //Sends data to backend
    const submit = async e => {
        //Prevents page to reload
        e.preventDefault()

        if (type === "Edit") {
            let dataToSend = {
                title: name ? name : videogame.name,
                description: description ? description : videogame.description,
                releaseDate: releaseDate ? releaseDate : videogame.releaseDate,
                lastUpdate: FormatLastUpdateDate(new Date())
            };
            await axios.put(`http://localhost:4567/editgame/${videogameID.videogameID}`, dataToSend, {
                headers: {
                        'Content-Type': 'application/json',
                        'token': item
                }
            });
        } else if (type === "Add") {
            let dataToSend = {
                name: name,
                description: description,
                releaseDate: releaseDate,
                lastUpdate: FormatLastUpdateDate(new Date())
            };
            await axios.post("http://localhost:4567/newgame", dataToSend, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': item
                }
            });
        }
        setNavigate(true);
    }

    const cancel = () => {
        setNavigate(true);
    }

/*
    const deleteGame = async () => {
        console.log(1);
        await axios.delete(`http://localhost:4567/deletegame/${videogameID.videogameID}`)
            .then(response => {
                console.log(response.data);
                console.log(videogame);
                setVideogame({});
                console.log(videogame);
            })
        console.log(videogame);
        //setNavigate(true);
    }
*/

    if(navigate) {
        return <Navigate to={"/"}/>;
    }

    return (
        <form className={"mainPopUP"} onSubmit={submit}>
            <h1>{type} videogame</h1>

            {/*}
            <div className={"cover"}>
                <h3>Upload cover</h3>
                <input name={"gameCover"} type={"file"} accept={"image/*"}
                    onChange={e => setImage(e.target.files[0])}
                />
            </div>
            */}

            <div className={"titleDesc"}>
                <input type={"text"} placeholder={"Add title"} defaultValue={videogame.name}
                       onChange={e => setName(e.target.value)}
                />
                <input id={"desc"} type={"text"} placeholder={"Add description"} defaultValue={videogame.description}
                       onChange={e => setDescription(e.target.value)}
                />
            </div>

            <div className={"releaseDate"}>
                <h3>Release date</h3>
                <input type={"datetime-local"} name={"releaseDate"} defaultValue={videogame.releaseDate}
                       onChange={e => setReleaseDate(e.target.value)}
                />
            </div>

{/*
            <div className={"platforms"}>
                <h3>Platforms</h3>
                <select name={"Platforms"} multiple>
                    <option value={"steam"}>Steam</option>
                    <option value={"xboxSeriesS/X"}>Xbox Series S/X</option>
                    <option value={"playstation5"}>Playstation 5</option>
                    <option value={"nintendoSwitch"}>Nintendo Switch</option>
                </select>
            </div>

            <div className={"tags"}>
                <h3>Tags</h3>
                <select name={"tags"} multiple>
                    <option value={"shooter"}>Shooter</option>
                    <option value={"firstPerson"}>First Person</option>
                    <option value={"survivalHorror"}>Survival Horror</option>
                    <option value={"rogueLike"}>Rogue-Like</option>
                </select>
            </div>
*/}
            <div className={"buttons"}>
                <input type={"button"} value={"Cancel"} onClick={cancel} />
                {/*{type === "Edit" ? <input type={"button"} value={"Delete"} onClick = {deleteGame} /> : null}*/}
                <input type={"button"} value={"Add"} onClick={submit} />
            </div>
        </form>
    );
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