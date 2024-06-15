import React, { useEffect, useRef, useState } from 'react';
import { Link } from "react-router-dom";
import axios from "axios";
import './AddGamePopUp.css';
import plus_icon from "../Assets/plus-icon.png";
import Banner from './Banner';
import LatestUpdated from './LatestUpdated';
import GamesFromDB from './GamesFromDB';
import Header from "../Header/Header";
import GenreList from './GenreList';
import GlobalSkeleton from './skeletons/GlobalSkeleton';

function Library() {
    const [gamesFromDB, setGamesFromDB] = useState([]);
    const [latestUpdated, setLatestUpdated] = useState([]);
    const [genreList, setGenreList] = useState([]);
    const [activeIndex, setActivateIndex] = useState(0);
    const [gamesByGenreId, setGamesByGenreId] = useState([]);
    const [activeGenreName, setActiveGenreName] = useState('Indie');
    const gamesByGenreRef = useRef(null);
    const [isDeveloper, setIsDeveloper] = useState(false);
    const [loading, setLoading] = useState(true); // Global loading state

    useEffect(() => {
        fetchData().then(() => console.log("Data fetched")).catch(e => console.error("Error fetching data:", e));
    }, []);

    async function fetchData() {
        await Promise.all([
            // Fetch user role
            getIsDeveloper(),

            // Fetch games
            fetchGamesFromDB(),
            fetchLatestUpdated(),
            fetchGamesByGenreId(9999, 'Indie'),
            fetchGenreList(),
        ]);
        setLoading(false);

    }

    async function getIsDeveloper() {
        try {
            const response = await axios.get(`http://localhost:4567/user/get/${localStorage.getItem('id')}`);
            if (response.data.rol === "DEVELOPER" || response.data.rol === "ADMIN") {
                setIsDeveloper(true);
            } else {
                setIsDeveloper(false);
            }
        } catch {
            setIsDeveloper(false);
        }
    }

    async function fetchGenreList() {
        try {
            const response = await axios.get('http://localhost:4567/tag/get/genres');
            setGenreList(response.data);
        } catch (error) {
            console.error("Error fetching genre list:", error);
        }
    }

    async function fetchGamesByGenreId(genreId, genreName) {
        try {
            const response = await axios.get(`http://localhost:4567/game/get/tag/${genreId}`);
            setGamesByGenreId(response.data);
            setActiveGenreName(genreName);
            if (gamesByGenreRef.current) {
                gamesByGenreRef.current.scrollIntoView({ behavior: 'smooth' });
            }
        } catch (error) {
            console.error("Error fetching games by genre ID:", error);
            return [];
        }
    }

    async function fetchGamesFromDB() {
        try {
            const response = await axios.get('http://localhost:4567/games');
            setGamesFromDB(response.data);
        } catch (error) {
            console.error("Error fetching games:", error);
        }
    }

    async function fetchLatestUpdated() {
        try {
            const response = await axios.get('http://localhost:4567/latestupdated/4');
            setLatestUpdated(response.data);
        } catch (error) {
            console.error("Error fetching latest updated games:", error);
        }
    }

    if (loading) {
        return <GlobalSkeleton />;
    }

    return (
        <div>
            <Header />
            <div className="grid grid-cols-4 p-5 bg-gray-200">
                <GenreList
                    genreList={genreList}
                    activeIndex={activeIndex}
                    setActivateIndex={setActivateIndex}
                    fetchGamesByGenreId={fetchGamesByGenreId}
                />
                <div className="col-span-4 md:col-span-3">
                    {gamesFromDB.length > 0 ? (
                        <div>
                            <Banner gameBanner={gamesFromDB[0]} />
                            <LatestUpdated gameList={latestUpdated} />
                            <div className="mt-10">
                                <GamesFromDB gamesFromDB={gamesFromDB} title="Games" />
                            </div>
                            <div ref={gamesByGenreRef} className="mt-10">
                                <GamesFromDB gamesFromDB={gamesByGenreId} title={activeGenreName} />
                            </div>
                        </div>
                    ) : null}
                </div>
                {isDeveloper ?
                    <Link className={"addVideogameButton"} to={"/addvideogame"}>
                        <img alt={'Add videogame'} className={"p-1"}
                             title={'Add a game to the library!'}
                             src={plus_icon} />
                    </Link>
                    :
                    null
                }
            </div>
        </div>
    );
}

export default Library;
