import React, {useEffect, useState} from "react";
import axios from "axios";
import AddNewPopUp from "./AddNewPopUp";

function NewsComp(props) {
    const [news, setNews] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        axios.get(`http://localhost:4567//news/get/game/${props.videogameID}`)
            .then(response => {
                setNews(response.data);
                console.log(response.data);
                setIsLoading(false);
            })
            .catch(error => {
                console.error('Error:', error);
                return (
                    <div>
                        <h2>No News to load.</h2>
                    </div>
                );
            });
    }, [props.videogameID]);

    const handleNewDeletion = (newsId, e) => {

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

    return (
        <div>
            {props.owner ?
                <AddNewPopUp videogameID={props.videogameID}/>
                :
                null
            }

            {isLoading ?
                <div>Loading...</div>
                :
                <div>
                    <h2>News</h2>
                    {news.length === 0 ?
                        <h3>Nothing new to see here!</h3>
                        :
                        <ul className={'particularNewDiv'}>
                            {news.map((news, index) => {
                                return (
                                    <li key={index}>
                                        <h3>{news.title}</h3>
                                        <p>{news.description}</p>
                                        {props.owner ?
                                            <button onClick={() => handleNewDeletion(news.id)}>Delete</button>
                                            :
                                            null
                                        }
                                    </li>
                                );
                            })}
                        </ul>
                    }
                </div>
            }
        </div>
    );
}

export default NewsComp;
