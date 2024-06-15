import React, { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
import SkeletonLoader from './skeletons/LatestUpdatedSkeleton';

function LatestUpdated({ gameList }) {
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        setTimeout(() => {
            setIsLoading(false);
        }, 200);
    }, []);

    return (
        <div className='mt-5 pr-2 bg-gray-200 '>
            <h2 className={'font-schibsted text-[30px] text-black pt-5 pb-7'}>Latest Updated</h2>
            <div className={'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5'}>
                {isLoading ? (
                    Array.from({ length: 4 }).map((_, index) => (
                        <SkeletonLoader key={index} />
                    ))
                ) : (
                    gameList.games.map((game, index) => index < 4 && (
                        <div key={game.id} className={'border-2 border-black bg-black rounded-xl overflow-hidden shadow-lg hover:scale-105 transition-all duration-300 ease-in-out cursor-pointer'}>
                            <Link to={'/videogame/' + game.id}>
                                <img src={game.cover} className={'h-[450px] w-full object-cover'} alt={game.name} />
                                <div className='p-3'>
                                    <h2 className={'text-white text-[23px] font-bold'}>{game.name}</h2>
                                </div>
                            </Link>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default LatestUpdated;
