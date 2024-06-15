import React, { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
import SkeletonLoader from './skeletons/BannerSkeleton';

function Banner({ gameBanner }) {
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        setTimeout(() => {
            setIsLoading(false);
        }, 500);
    }, []);

    return (
        <div className='relative bg-black p-0.5 rounded-xl shadow-lg'>
            {isLoading ? (
                <SkeletonLoader />
            ) : (
                <Link to={'/videogame/' + gameBanner.id}>
                    <div className={'absolute bottom-0 p-6 bg-gradient-to-t from-slate-900 to-transparent w-full rounded-b-xl'}>
                        <h2 className={'text-[40px] text-white font-schibsted py-1'}>{gameBanner.name}</h2>
                        <button className={'bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-md transition-all duration-300'}>
                            Check it out!
                        </button>
                    </div>
                    <img src={gameBanner.background_image} className='md:w-full h-[500px] object-cover rounded-xl' alt={gameBanner.name} />
                </Link>
            )}
        </div>
    );
}

export default Banner;
