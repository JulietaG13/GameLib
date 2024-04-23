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
        if (review === '') return;
        axios.post(`http://localhost:4567/newreview/${videogameID.videogameID}`, {
            'text': review
        }, {
            headers: {
                'Content-Type': 'application/json',
                'token': token
            }
        }).then(r => setReview(''));
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
                    <h1>{videogame.name}</h1>
                    <p>{videogame.description}</p>
                    <p>Date of release: {formatDate(videogame.releaseDate)}</p>
                </div>

            </div>

            <form className={'reviews'} onSubmit={publishReview} >
                <input id={'1'} type={'text'} placeholder={'Add your review'}
                       value={review}
                    onChange={e =>
                        setReview(e.target.value)
                    }
                />
                <input id={'2'} type={"button"} value={"Publish"} onClick={publishReview} />
            </form>

        </div>
    );
}

function formatDate(date) {
    let d = new Date(date);
    return d.getDate() + "/" + (d.getMonth()) + "/" + d.getFullYear() + ", " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + " GMT";
}

export default VideogameView;

// {token !== null ? <input type={"button"} value={"Edit game"} onClick={edit}/> : null}