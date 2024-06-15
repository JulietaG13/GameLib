import React from 'react';

function GenreList({ genreList, activeIndex, setActivateIndex, fetchGamesByGenreId }) {
    return (
        <div className="col-span-4 md:col-span-1">
            <div className="pr-10 sticky top-0">
                <h2 className="text-[30px] font-bold mb-1">Genres</h2>
                {genreList.map((genre, index) => (
                    <div
                        key={index}
                        onClick={() => {
                            setActivateIndex(index);
                            fetchGamesByGenreId(genre.id, genre.name);
                        }}
                        className={`border-2 border-black hover:text-xl text-black flex gap-1 items-center mb-3 cursor-pointer hover:bg-gray-800 hover:rounded-xl p-5 rounded-lg group ${activeIndex === index ? "bg-gray-950 text-white rounded-xl" : ""}`}
                    >
                        {/*<img src={genre.background_image}
                             className={`w-[50px] h-[50px] object-cover rounded-lg group-hover:scale-110 transition-all ease-out duration-300 ${activeIndex === index ? "scale-110" : ""}`}
                             alt="genre_image" />*/}
                        <h3 className={`transition-all ease-out duration-300 ${activeIndex === index ? "font-bold text-white" : "text-black group-hover:text-white"}`}>
                            {genre.name}
                        </h3>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default GenreList;
