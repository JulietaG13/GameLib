import React, {useState} from "react";
import './ManageVideogame.css';

function ManageVideogame() {
    return (
        <div className={"mainPopUP"}>
            <h1>Add videogame</h1>

            <div className={"cover"}>
                <h3>Upload cover</h3>
                <input name={"gameCover"} type={"file"} accept={"image/*"}/>
            </div>

            <div className={"titleDesc"}>
                <input type={"text"} placeholder={"Add title"}/>
                <input id={"desc"} type={"text"} placeholder={"Add description"}/>
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
                <input type={"button"} value={"Delete"}/>
                <input type={"button"} value={"Add"}/>
            </div>
        </div>
    );
}

export default ManageVideogame;