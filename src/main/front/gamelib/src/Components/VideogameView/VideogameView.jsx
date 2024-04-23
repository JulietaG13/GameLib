import React, {useEffect, useState} from "react";
import {Navigate, useParams} from "react-router-dom";
import axios from "axios";
import './VideogameView.css';
import standByCover from '../Assets/standByCover.png';

function VideogameView() {
    const videogameID = useParams();
    const [videogame, setVideogame] = useState({});
    const [review, setReview] = useState('');
    const token = localStorage.getItem('token');
    const [navigate, setNavigate] = useState(false);

    useEffect(() => {
        axios.get(`http://localhost:4567/getgame/${videogameID.videogameID}`)
            .then(response => {
                setVideogame(response.data);
                console.log(response.data);
            })
    }, []);

    const publishReview = () => {

    }

    const edit = () => {
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': token
            }
        }).then(response => {
            if(response.status === 200) {
                setNavigate(true);
            }
        });
    }

    if(navigate) {
        return <Navigate to={`/editVideogame/${videogameID.videogameID}`}/>;
    }

    return (
        <div className={'gameView'} >

            <div className={'gameData'}>

                <div className={'gamePicture'}>
                    <img src={standByCover} alt={'Cover'}/>
                </div>

                <div className={'gameAttributes'}>
                    {token !== null ? <input type={"button"} value={"Edit game"} onClick={edit}/> : null}
                    <h1>{videogame.title}</h1>
                    <p>{videogame.description}</p>
                    <p>Date of release: {formatDate(videogame.releaseDate)}</p>
                </div>

            </div>

            <div className={'reviews'} >
                <input type={'text'} placeholder={'Add your review'} onClick={publishReview}
                onChange={e => setReview(e.target.value)}
                />
            </div>

        </div>
    );
}

function formatDate(date) {
    let d = new Date(date);
    return d.getDate() + "/" + (d.getMonth()) + "/" + d.getFullYear() + ", " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + " GMT";
}

export default VideogameView;