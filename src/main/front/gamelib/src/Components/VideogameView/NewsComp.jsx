import React, {useEffect, useState} from "react";
import axios from "axios";
import AddNewPopUp from "./AddNewPopUp";
import SkeletonComp from "./skeleton/SkeletonComp";

function NewsComp(props) {
    const [news, setNews] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        axios.get(`http://localhost:4567/news/get/game/${props.videogameID}`)
            .then(response => {
                setNews(response.data);
                console.log(response.data);
                console.log(response.data.length);
                setIsLoading(false);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }, [props.videogameID]);

    const handleNewDeletion = (newsId) => {

        axios.put(`http://localhost:4567/news/delete/id/${newsId}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                console.log(response.data);
                setNews(news.filter(news => news.id !== newsId));
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    return (!isLoading ?
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <h2 style={{ marginTop: '0.1em', marginRight: 'auto', fontWeight: 'bold' }}>News</h2>
                        {props.owner ?
                            <div style={{ flex: 1, textAlign: 'right' }}>
                                <AddNewPopUp videogameID={props.videogameID}/>
                            </div>
                            :
                            null
                        }
                    </div>
                    {news.length === 0 ?
                        <h3 id={'noNews'}>Nothing to see here!</h3>
                        :
                        <ul className={'particularNewDiv'}>
                            {news.map((news, index) => {
                                return (
                                    <li key={index}
                                        style={{overflow: 'hidden', textOverflow: 'ellipsis', wordBreak: 'keep-all'}}>
                                        <h3 style={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            wordBreak: 'keep-all'
                                        }}>{news.title}</h3>
                                        <p style={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            wordBreak: 'keep-all'
                                        }}>{news.description}</p>
                                        {props.owner ? (
                                            <button className={'deletionButton'}
                                                    onClick={() => handleNewDeletion(news.id)}>
                                                Delete
                                            </button>
                                        ) : null}
                                    </li>
                                );
                            })}
                        </ul>
                    }
                </div>
                :
                <SkeletonComp/>
    );
}

export default NewsComp;
