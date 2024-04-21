import React, {useEffect, useState} from "react";
import './ManageVideogame.css';
import axios from "axios";
import {Navigate, useParams} from "react-router-dom";

function ManageVideogame({type}) {
    const videogameID = useParams();
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [releaseDate, setReleaseDate] = useState('');
    const [navigate, setNavigate] = useState(false);
    const [videogame, setVideogame] = useState({});

    useEffect(() => {
        if(type === "Edit") {
            axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
                .then(response => {
                    setVideogame(response.data);
                })
        }
    }, [])

    console.log(videogame)
    console.log(videogame.releaseDate)

    //Sends data to backend
    const submit = async e => {
        //Prevents page to reload
        e.preventDefault()

        await axios.post("http://localhost:4567/newgame", {
            title: title, description: description, releaseDate: releaseDate
        });

        // if (type === "Edit") {
        //     await axios.put(`http://localhost:4567/editgame/${videogameID.videogameID}`, {
        //         title: title, description: description, releaseDate: releaseDate, lastUpdate: releaseDate
        //     });
        // } else if (type === "Add") {
        //     await axios.post("http://localhost:4567/newgame", {
        //         title: title, description: description, releaseDate: releaseDate
        //     });
        // }

        setNavigate(true);
    }

    const cancel = () => {
        setNavigate(true);
    }


    if(navigate) {
        return <Navigate to={"/"}/>;
    }

    return (
        <form className={"mainPopUP"} onSubmit={submit}>
            <h1>{type} videogame</h1>

            {/*
            <div className={"cover"}>
                <h3>Upload cover</h3>
                <input name={"gameCover"} type={"file"} accept={"image/*"}/>
            </div>
            */}

            <div className={"titleDesc"}>
                <input type={"text"} placeholder={"Add title"} defaultValue={videogame.title}
                       onChange={e => setTitle(e.target.value)}
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
                <input type={"button"} value={"Add"} onClick={submit} />
            </div>
        </form>
    );
}

export default ManageVideogame;