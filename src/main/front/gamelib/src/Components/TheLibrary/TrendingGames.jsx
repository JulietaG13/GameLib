import React, {useEffect} from 'react'

function TrendingGames({gameList}) {

    return (
        <div className='mt-5 pr-2 hidden md:block'>
            <h2 className={'font-bold text-[30px] text-white pt-5 pb-7'}> Trending Games</h2>
            <div className={'hidden md:grid md:grid-cols-3  gap-5 lg:grid-cols-4'}>
                {gameList.map((game, index) => index<4&&(
                    <div className={'bg-[#76a8f75e] rounded-xl p-0.1 hover:scale-110 transition-all duration-300 ease-in-out'}>
                        <img src={game.background_image} className={'h-[500px] rounded-lg object-cover'}/>
                        <h2 className={'text-white text-[23px] font-bold px-3'}>{game.name}</h2>
                    </div>
                ))}
            </div>
        </div>
        )
}
export default TrendingGames