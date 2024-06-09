import React, {useEffect, useState} from 'react'
import axios from 'axios'


function GenreList({genreId}) {
    const [genreList, setGenreList] = useState([]);
    const [activeIndex, setActivateIndex] = useState(0);

    // Get the list of genres when the component is mounted
    useEffect(() => {
        axios.get('http://localhost:4567/tag/get/genres').then((response) =>{
            setGenreList(response.data);
        })
    }, []);


    const getGamesByGenreId = async (genreId) => {
        try {
            const response = await axios.get(`http://localhost:4567/game/get/tag/${genreId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching games by genre ID:", error);
            return [];
        }
    };

    return(
        <div className={'pr-40'}>
            <h2 className='text-[30px] font-bold text-black'>Genres</h2>
            {genreList.map((genre, index) => (
                <div
                    // Set the active index and genre id when a genre is clicked
                    onClick={()=> {setActivateIndex(index); getGamesByGenreId(genre.id)}}
                    className={`text-black flex gap-2 items-center mb-2 cursor-pointer hover:bg-gray-800 hover:text-white hover:rounded-xl p-2 ' +
                    'rounded-lg group' ${activeIndex===index?"bg-gray-950 rounded-xl":null}`} >
                    <img src={genre.image_background}
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
        )
}

export default GenreList;