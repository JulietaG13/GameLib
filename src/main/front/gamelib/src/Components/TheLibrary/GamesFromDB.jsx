import React, { useRef } from 'react';
import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';

function GamesFromDB({ gamesFromDB }) {
    const scrollRef = useRef(null);

    useEffect(() => {
        console.log("gameListByGenres", gamesFromDB);
    });

    const scrollLeft = () => {
        scrollRef.current.scrollLeft -= 300;
    };

    const scrollRight = () => {
        scrollRef.current.scrollLeft += 300;
    };

    return (
        <div className="bg-gray-200 relative">
            <h2 className="font-bold text-[30px] text-black pt-10 pb-5">Popular Games</h2>

            {/* Left Arrow */}
            <button className="absolute top-1/2 left-4 z-10 transform -translate-y-1/2 bg-gray-400 hover:bg-gray-500 text-white rounded-full p-2" onClick={scrollLeft}>
                <FontAwesomeIcon icon={faChevronLeft} />
            </button>

            <div className="flex overflow-x-auto space-x-6" ref={scrollRef} style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}>
                {gamesFromDB.map((game) => (
                    <Link key={game.id} to={'/videogame/' + game.id}>
                        <div className="flex-shrink-0 w-[250px] bg-black p-1.5 rounded-lg hover:scale-110 ease-in-out duration-300 cursor-pointer"> {/* Fixed width and flex-shrink-0 */}
                            <img src={game.background_image} className="w-full h-[400px] rounded-xl object-cover" alt={game.name} />
                            <h2 className="text-white pt-1 text-lg font-bold truncate">
                                {game.name}
                            </h2>
                        </div>
                    </Link>
                ))}
            </div>

            {/* Right Arrow */}
            <button className="absolute top-1/2 right-4 z-10 transform -translate-y-1/2 bg-gray-400 hover:bg-gray-500 text-white rounded-full p-2" onClick={scrollRight}>
                <FontAwesomeIcon icon={faChevronRight} />
            </button>
        </div>
    );
}

export default GamesFromDB;
