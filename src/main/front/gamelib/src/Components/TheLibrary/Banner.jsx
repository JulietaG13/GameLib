import React from 'react'

function Banner({gameBanner}) {

    return(
        <div className='relative bg-black p-0.5 rounded-xl'>
            <div className={'absolute bottom-0 p-6 bg-gradient-to-t from-slate-900 to-transparent w-full'}>
                <h2 className={'text-[40px] text-white font-bold py-1'}>{gameBanner.name}</h2>
                <button className={'bg-black text-white px-2 p-1 rounded-md py-0.5'}>Check it out!</button>
            </div>
            <img src={gameBanner.background_image} className='md:w-full h-[500px] object-cover rounded-xl'/>
        </div>
    )
}
export default Banner;