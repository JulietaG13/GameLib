import React, {useEffect, useState} from 'react'
import GenreList from './GenreList'
import Banner from './Banner'
import TrendingGames from './TrendingGames'
import GamesByGenresId from './GamesByGenresId'
import GamesFromDB from './GamesFromDB'
import axios from "axios";
import HeaderV2 from "../Header/HeaderV2";
import Loader from "./Loader";


function Library() {
    const [gamesFromDB, setGamesFromDB] = useState([]);
    const [isLoading, setIsLoading] = useState(false)

    useEffect(() => {
        getGamesFromDB()
    }, []);

    const getGamesFromDB = () => {
        setIsLoading(true)
        axios.get('http://localhost:4567/games').then((response) => {
            console.log("gamesFromDB:", response)
            setGamesFromDB(response.data)
            setIsLoading(false)
        });
    }

    return(
        <div>
            <HeaderV2/>
            {isLoading ? (
                <div
                    style={{
                        width: "100px",
                        margin: "auto",
                        marginTop: "20px"
                    }}
                >
                    <Loader/>
                </div>
            ) : null }
            <div className='grid grid-cols-4 p-5 bg-gray-200  '>
                <div className='h-full hidden md:block'>
                    {/*<GenreList genreId={(genreId) => getGameListByGenreId(genreId)}/>*/}
                </div>
                <div className='cold-span-4 md:col-span-3'>
                    {gamesFromDB.length>0?
                        <div>
                            <Banner gameBanner={gamesFromDB[0]}/>
                            <TrendingGames gameList={gamesFromDB}/>
                            <GamesByGenresId gameList={gamesFromDB}/>
                            <GamesFromDB gamesFromDB={gamesFromDB}/>
                        </div>
                    :null}
                </div>
            </div>
        </div>
    )
}
export default Library;
