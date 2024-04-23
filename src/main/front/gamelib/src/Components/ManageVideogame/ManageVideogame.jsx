import React, {useEffect, useState} from "react";
import './ManageVideogame.css';
import axios from "axios";
import {Navigate, useParams} from "react-router-dom";

function ManageVideogame({type}) {
    const videogameID = useParams();
    //TODO: Add image upload
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [releaseDate, setReleaseDate] = useState('');
    const [navigate, setNavigate] = useState(false);
    const [videogame, setVideogame] = useState({});
    let item = localStorage.getItem('token');


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
                        .catch(error => {
                            console.error('Error:', error);
                            setNavigate(true);
                        })
                        .then(response => {
                            setVideogame(response.data);
                            //console.log(response.data);
                        });
                }
            });
    }, []);


    //Sends data to backend
    const submit = async e => {
        //Prevents page to reload
        e.preventDefault()

        if (type === "Edit") {
            let dataToSend = {
                name: name ? name : videogame.name,
                description: description ? description : videogame.description,
                releaseDate: releaseDate ? releaseDate : videogame.releaseDate,
                lastUpdate: FormatLastUpdateDate(new Date())
            };
            await axios.put(`http://localhost:4567/editgame/${videogameID.videogameID}`, dataToSend, {
                headers: {
                        'Content-Type': 'application/json',
                        'token': item
                }
            }).then(r => setVideogame({}));
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
            }).then(r => setVideogame({}));
        }
        setNavigate(true);
    }

    const cancel = () => {
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
                setVideogame({});
                console.log("post del game:");
                console.log(videogame);
            })
        console.log("one more time: ");
        console.log(videogame);
        setNavigate(true);
    }

    useEffect(() => {
        console.log("videogame has been updated: ");
        console.log(videogame);
    }, [videogame]);

    if(navigate) {
        return <Navigate to={"/"}/>;
    }

    return (
        <form className={"mainPopUP flex flex-col items-center"} onSubmit={submit} style={{ width: "50%", justifyContent: 'center' }}>
            <h1 className={'font-bold text-[30px] mb-2 text-center'}>{type} Videogame</h1>
            <div className={"titleDesc flex justify-center items-center"}>
                <input className={'p-1 rounded mb-2'} type={"text"} placeholder={"Add title"} defaultValue={videogame.name}
                       onChange={e => setName(e.target.value)}
                />
                <input id={"desc"} type={"text"} className={'p-1 rounded mb-2'} placeholder={"Add description"} defaultValue={videogame.description}
                       onChange={e => setDescription(e.target.value)}
                />
            </div>

            <div className={"releaseDate font-bold flex justify-start items-center mb-2"}>
                <div className={'flex justify-center'}>
                    <input type={"datetime-local"} className={'rounded-b'} name={"releaseDate"} defaultValue={videogame.releaseDate}
                           onChange={e => setReleaseDate(e.target.value)}
                    />
                </div>
            </div>
            <div className={"font-bold flex justify-center"}>
                <input type={"button"} className={'submit cursor-pointer mr-2'} value={"Cancel"} onClick={cancel} />
                {type === "Edit" ? <input type={"button"} value={"Delete"} onClick = {deleteGame} /> : null}
                <input type={"button"} value={"Add"} className={'submit cursor-pointer'} onClick={submit} />
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