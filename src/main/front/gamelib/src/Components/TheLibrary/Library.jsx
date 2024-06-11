import React, { useEffect, useState } from 'react';
import {Link} from "react-router-dom";
import axios from "axios";
import './AddGamePopUp.css'
import plus_icon from "../Assets/plus-icon.png";
import Banner from './Banner';
import TrendingGames from './TrendingGames';
import GamesFromDB from './GamesFromDB';
import Header from "../Header/Header";
import SkeletonLoader from "./SkeletonLoader";

function Library() {
    const [gamesFromDB, setGamesFromDB] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [genreList, setGenreList] = useState([]);
    const [activeIndex, setActivateIndex] = useState(0);
    const [gamesByGenreId, setGamesByGenreId] = useState([]);
    const [activeGenreName, setActiveGenreName] = useState('Action');

    const [isDeveloper, setIsDeveloper] = useState(false);

    useEffect(() => {
        getIsDeveloper().then(r => {});
    }, []);

    const getIsDeveloper = async () => {
        axios.get(`http://localhost:4567/user/get/${localStorage.getItem('id')}`)
            .then((response) => {
                if (response.data.rol === "DEVELOPER" || response.data.rol === "ADMIN") {
                    setIsDeveloper(true);
                } else {
                    setIsDeveloper(false);
                }
            })
            .catch(() => {
                setIsDeveloper(false);
            })
    }

    useEffect(() => {
        axios.get('http://localhost:4567/tag/get/genres').then((response) => {
            setGenreList(response.data);
        });
    }, []);

    const getGamesByGenreId = async (genreId, genreName) => {
        try {
            const response = await axios.get(`http://localhost:4567/game/get/tag/${genreId}`);
            setGamesByGenreId(response.data);
            setActiveGenreName(genreName);
        } catch (error) {
            console.error("Error fetching games by genre ID:", error);
            return [];
        }
    };

    useEffect(() => {
        getGamesFromDB();
        getGamesByGenreId(26, 'Action').then(() => { }); // Default genre
    }, []);

    const getGamesFromDB = () => {
        setIsLoading(true);
        axios.get('http://localhost:4567/games').then((response) => {
            setGamesFromDB(response.data);
            setIsLoading(false);
        }).catch((error) => {
            console.error("Error fetching games:", error);
            setIsLoading(false);
        });
    };

    return (
        <div>
            {isLoading ? (
                <SkeletonLoader />
            ) : (

                <div>
                    <Header />
                <div className="grid grid-cols-4 p-5 bg-gray-200">
                    <div className="col-span-4 md:col-span-1">
                        <div className="pr-10 sticky top-0">
                            <h2 className="text-[30px] font-bold mb-1">Genres</h2>
                            {genreList.map((genre, index) => (
                                <div
                                    key={index}
                                    onClick={() => {
                                        setActivateIndex(index);
                                        getGamesByGenreId(genre.id, genre.name).then(() => { });
                                    }}
                                    className={`border-2 border-black  hover:text-xl text-black flex gap-1 items-center mb-3 cursor-pointer hover:bg-gray-800  hover:rounded-xl p-5 rounded-lg group ${activeIndex === index ? "bg-gray-950 text-white rounded-xl" : ""}`}
                                >
                                    {/*}
                                    <img src={genre.background_image}
                                         className={`w-[50px] h-[50px] object-cover rounded-lg group-hover:scale-110 transition-all ease-out duration-300 ${activeIndex === index ? "scale-110" : ""}`}
                                         alt="genre_image" />*/}
                                    <h3 className={`transition-all ease-out duration-300 ${activeIndex === index ? "font-bold text-white" : "text-black group-hover:text-white"}`}>                                        {genre.name}
                                    </h3>
                                </div>
                            ))}
                        </div>
                    </div>
                    <div className="col-span-4 md:col-span-3">
                        {gamesFromDB.length > 0 ? (
                            <div>
                                <Banner gameBanner={gamesFromDB[0]}/>
                                <TrendingGames gameList={gamesFromDB}/>
                                <div className="mt-10">
                                    <GamesFromDB gamesFromDB={gamesFromDB} title="Popular Games"/>
                                </div>
                                <div className="mt-10">
                                    <GamesFromDB gamesFromDB={gamesByGenreId} title={activeGenreName}/>
                                </div>
                            </div>
                        ) : null}
                    </div>
                    {isDeveloper ?
                        <Link className={"addVideogameButton"} to={"/addvideogame"}>
                            <img alt={'Add videogame'} className={"p-1"}
                                 title={'Add a game to the library!'}
                                 src={plus_icon}/>
                        </Link>
                        :
                        null
                    }
                </div>
                </div>
            )}
        </div>
    );
}

export default Library;
