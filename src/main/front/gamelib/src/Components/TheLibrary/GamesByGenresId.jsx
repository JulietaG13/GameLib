import {useEffect} from 'react';
import {Link} from "react-router-dom";

function GamesByGenresId({gameList}) {



    return (
        <div className={' bg-gray-200'}>
            <h2 className={'font-bold text-[30px] text-black pt-10 pb-5' }>Games By Genre</h2>
            <div className={'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'}>
                {gameList.map((game) => (
                    <div className={'bg-black p-1.5 rounded-lg hover:scale-110 ease-in-out duration-300 cursor-pointer'}>
                        <Link to={'/videogame/' + game.id}>
                            <img src={game.background_image} className={'w-full h-[260px] rounded-xl object-cover'}/>
                            <h2 className={'text-white pt-1 text-[20px] font-bold'}>
                                {game.name}
                            </h2>
                        </Link>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default GamesByGenresId