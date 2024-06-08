import React, {useEffect, useState} from 'react'
import Banner from './Banner'
import TrendingGames from './TrendingGames'
import GamesFromDB from './GamesFromDB'
import axios from "axios";
import HeaderV2 from "../Header/HeaderV2";
import Loader from "./Loader";

function Library() {
    const [gamesFromDB, setGamesFromDB] = useState([]);
    const [isLoading, setIsLoading] = useState(false)
    const [genreList, setGenreList] = useState([]);
    const [activeIndex, setActivateIndex] = useState(0);
    const [gamesByGenreId, setGamesByGenreId] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:4567/tag/get/genres').then((response) =>{
            console.log("All Genres: ", response.data);
            setGenreList(response.data);
        })
    }, []);


    const getGamesByGenreId = async (genreId) => {
        try {
            const response = await axios.get(`http://localhost:4567/game/get/tag/${genreId}`);
            console.log("Games by genre ID:", response.data)
            setGamesByGenreId(response.data)
        } catch (error) {
            console.error("Error fetching games by genre ID:", error);
            return [];
        }
    };

    useEffect(() => {
        getGamesFromDB()
        getGamesByGenreId(26)
    }, []);

    const getGamesFromDB = () => {
        setIsLoading(true)
        axios.get('http://localhost:4567/games').then((response) => {
            console.log("gamesFromDB:", response)
            setGamesFromDB(response.data)
            setIsLoading(false)
        }).catch((error) => {
            console.error("Error fetching games:", error)
        })
        ;
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
                    <div className={'pr-40'}>
                        <h2 className='text-[30px] font-bold text-black'>Genres</h2>
                        {genreList.map((genre, index) => (
                            <div
                                // Set the active index and genre id when a genre is clicked
                                onClick={()=> {setActivateIndex(index); getGamesByGenreId(genre.id)}}
                                className={`text-black flex gap-2 items-center mb-2 cursor-pointer hover:bg-gray-800 hover:text-white hover:rounded-xl p-2 ' +
                    'rounded-lg group' ${activeIndex===index?"bg-gray-950 rounded-xl":null}`} >
                                <img src={genre.background_image}
                                     className={`w-[40px] h-[40px]
                        object-cover rounded-lg group-hover:scale-105 transition-all
                        ease-out duration-300 ${activeIndex === index ? "scale-105" : null}`}
                                 alt={'genre_image'}/>
                                <h3 className={`group-hover:font-bold transition-all
                        ease-out duration-300 ${activeIndex === index ? "font-bold text-white" : null}`}>{genre.name}
                                </h3>
                            </div>
                        ))}
                    </div>
                </div>
                <div className='cold-span-4 md:col-span-3'>
                    {gamesFromDB.length>0?
                        <div>
                            <Banner gameBanner={gamesFromDB[0]}/>
                            <TrendingGames gameList={gamesFromDB}/>
                            <GamesFromDB gamesFromDB={gamesFromDB}/>
                            <GamesFromDB gamesFromDB={gamesByGenreId}/>

                        </div>
                    :null}
                </div>
            </div>
        </div>
    )
}
export default Library;
