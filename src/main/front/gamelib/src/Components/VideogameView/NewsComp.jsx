import React, {useEffect, useState} from "react";
import axios from "axios";

function NewsComp(videogameID) {
    const [news, setNews] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        axios.get(`http://localhost:4567//news/get/game/${videogameID.videogameID}`)
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
    }, [videogameID]);

    return (
        <div>
            {isLoading ? <div>Loading...</div> :
                <div>
                    <h2>News</h2>
                    {news.length === 0 ?
                        <h3>Nothing new to see here!</h3>
                        :
                        <ul>
                            {news.map((news, index) => {
                                return (
                                    <li key={index}>
                                        <h3>{news.title}</h3>
                                        <p>{news.description}</p>
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
