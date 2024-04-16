import React, {useState} from "react";
import './ManageVideogame.css';
import axios from "axios";

function ManageVideogame({type, videogameID}) {
    console.log('id:', videogameID);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');

    //sends data to backend
    const submit = async e => {
        //prevents page to reload
        e.preventDefault()

        await axios.post("http://localhost:4567/newgame", {
            title: title, description: description
        });
    }

    return (
        <form className={"mainPopUP"} onSubmit={submit}>
            <h1>{type} videogame{videogameID}</h1>

            <div className={"cover"}>
                <h3>Upload cover</h3>
                <input name={"gameCover"} type={"file"} accept={"image/*"}/>
            </div>

            <div className={"titleDesc"}>
                <input type={"text"} placeholder={"Add title"}
                       onChange={e => setTitle(e.target.value)}
                />
                <input id={"desc"} type={"text"} placeholder={"Add description"}
                       onChange={e => setDescription(e.target.value)}
                />
            </div>

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

            <div className={"buttons"}>
                <input type={"button"} value={"Cancel"}/>
                <input type={"button"} value={"Add"} onClick={submit} />
            </div>
        </form>
    );
}

export default ManageVideogame;