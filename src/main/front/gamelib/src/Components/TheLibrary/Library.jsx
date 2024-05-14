import React, {useEffect, useState} from 'react'
import GenreList from './GenreList'
import GlobalApiForTesting from './GlobalApiForTesting'
import Banner from './Banner'
import TrendingGames from './TrendingGames'
import GamesByGenresId from './GamesByGenresId'
import GamesFromDB from './GamesFromDB'
import axios from "axios";
import HeaderV2 from "../Header/HeaderV2";


//first tailwind attempt
function Library() {
    const [allGamesList, setAllGamesList] = useState([]);
    const [gameListByGenres, setGameListByGenres] = useState([]);
    const [gamesFromDB, setGamesFromDB] = useState([]);

    useEffect(() => {
        getAllGamesList();
        getGameListByGenreId(4)
        getGamesFromDB()
    }, []);

    const getGamesFromDB = () => {
        const response = axios.get('http://localhost:4567/games').then((response) => {
            console.log("gamesFromDB:", response)
            setGamesFromDB(response.data)
        })
    }

    const getAllGamesList = () => {
        GlobalApiForTesting.getAllGames.then((response) => {
            //replace with our own api
            console.log("allGamesList:", response);
            setAllGamesList(response.data.results);
            setGameListByGenres(response.data.results);
        })
    }

    const  getGameListByGenreId = (id) => {
        console.log("genreId:", id);
        GlobalApiForTesting.getGameListByGenreId(id).then((response) => {
            console.log("gameListByGenres:", response);
            setGameListByGenres(response.data.results);
        })
    }

    return(
        <div>
            <HeaderV2/>
            <div className='grid grid-cols-4 p-5 bg-gray-200  '>
                <div className='h-full hidden md:block'>
                    <GenreList genreId={(genreId) => getGameListByGenreId(genreId)}/>
                </div>
                <div className='cold-span-4 md:col-span-3'>
                    {allGamesList?.length>0 && gameListByGenres.length>0 && gamesFromDB.length>0?
                        <div>
                            <Banner gameBanner={allGamesList[8]}/>
                            <TrendingGames gameList={allGamesList}/>
                            <GamesByGenresId gameList={gameListByGenres}/>
                            <GamesFromDB gamesFromDB={gamesFromDB}/>
                        </div>
                    :null}
                </div>
            </div>
        </div>
    )
}
export default Library;
