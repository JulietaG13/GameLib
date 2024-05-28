import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

function Shelves({ username }) {
    const [shelves, setShelves] = useState([]);

    useEffect(() => {
        axios.get(`http://localhost:4567/shelf/get/user/${username}/10`)
            .then(r => setShelves(r.data))
            .catch(error => console.error("Error fetching shelves:", error));
    }, [username]);

    return (
        <div>
            {shelves.length === 0 ? (
                <div className="pl-16">
                    <h2 className="font-bold text-[30px] text-black pt-10 pb-5">User has no shelves</h2>
                </div>
            ) : (
                shelves.map((shelf) => (
                    <div key={shelf.id} className={"pl-16"}>
                        <div className={' bg-white'}>
                            <h2 className={'font-bold text-[30px] text-black pt-10 pb-5'}> {shelf.name}</h2>
                            <div className={'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-6'}>
                                {shelf.games.map((game) => (
                                    <Link to={'/videogame/' + game.id} key={game.id}>
                                        <div
                                            className={'bg-[#ff8341] p-1.5 rounded-lg hover:scale-110 ease-in-out duration-300 cursor-pointer w-[280px] h-[350px]'}>
                                            <img src={game.background_image}
                                                 className={'w-full h-[260px] rounded-xl object-cover'} alt={""} />
                                            <h2 className={'text-white pt-1 text-lg font-bold truncate'}>
                                                {game.name}
                                            </h2>
                                        </div>
                                    </Link>
                                ))}
                            </div>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}

export default Shelves;
