import React, {useEffect, useState} from 'react'
import axios from 'axios'
import GlobalApiForTesting from './GlobalApiForTesting'


function GenreList({genreId}) {

    const [genreList, setGenreList] = useState([]);
    const [activeIndex, setActivateIndex] = useState(0);

    // Get the list of genres when the component is mounted
    useEffect(() => {
        getGenreList()
    }, []);


    // Get the list of genres
    const getGenreList= () => {
        GlobalApiForTesting.getGenreList.then((response) => {
            //console.log(response.data);
            setGenreList(response.data.results);
        })
    }

    //our own api
    /*
    axios.get('http://localhost:4567/genre').then((response) =>{
        console.log(response.data);
        setGenreList(response.data.results);
    })
   /
 }
*/
    return(
        <div>
            <h2 className='text-[30px] font-bold text-black'>Genres</h2>
            {genreList.map((genre, index) => (
                <div
                    // Set the active index and genre id when a genre is clicked
                    onClick={()=> {setActivateIndex(index); genreId(genre.id)}}
                    className={`text-black flex gap-2 items-center mb-2 cursor-pointer hover:bg-gray-800 p-2 ' +
                    'rounded-lg group' ${activeIndex===index?"bg-gray-950":null}`}>
                    <img src={genre.image_background}
                        className={`w-[40px] h-[40px]
                        object-cover rounded-lg group-hover:scale-105 transition-all
                        ease-out duration-300 ${activeIndex === index ? "scale-105" : null}`}
                    />
                    <h3 className={`group-hover:font-bold transition-all
                        ease-out duration-300 ${activeIndex === index ? "font-bold text-white" : null}`}>{genre.name}
                    </h3>
                </div>
            ))}
        </div>
        )
}

export default GenreList;