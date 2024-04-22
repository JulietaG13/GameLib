import react, {useEffect} from 'react';

function GamesByGenresId({gameList}) {
    useEffect(() => {
        console.log("gameListByGenres", gameList);
    });

    return (
        <div>
            <h2 className={'font-bold text-[30px] text-white pt-10 pb-5' }> Popular Games</h2>
            <div className={'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'}>
                {gameList.map((game) => (
                    <div className={'bg-gray-950 p-3 rounded-lg hover:scale-110 ease-in-out duration-300 cursor-pointer'}>
                        <img src={game.background_image} className={'w-full h-[80%] rounded-xl object-cover'}/>
                        <h2 className={'text-white pt-1 text-[20px] font-bold'}>
                            {game.name}
                        </h2>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default GamesByGenresId