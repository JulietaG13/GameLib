import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

function Developed({ username }) {
    const [developed, setDeveloped] = useState([]);

    useEffect(() => {
        axios.get(`http://localhost:4567/dev/get/developed/${username}`, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(response => {
                console.log(response.data.developed);
                // Verifica que response.data sea un arreglo antes de asignarlo
                    setDeveloped(response.data.developed);
            })
            .catch(error => console.error("Error fetching developed games:", error));
    }, [username]);

    return (
        <div className={"mr-14"}>
            {developed.length === 0 ? (
                <div className="pl-16">
                    <h2 className="font-bold text-[25px] text-black pt-3 pb-5 pl-5">No developed games to display!</h2>
                </div>
            ) : (
                <div className="pl-16 pt-8 pb-1">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-8">
                        {developed.map((game) => (
                            <div key={game.id} className="relative">
                                <Link to={`/videogame/${game.id}`}>
                                    <div className="bg-[#ff8341] p-1.5 rounded-lg hover:scale-110 ease-in-out duration-300 cursor-pointer w-[280px] h-[350px]">
                                        <img src={game.cover} className="w-full h-[260px] rounded-xl object-cover" alt="" />
                                        <h2 className="text-white pt-1 text-lg font-bold truncate">
                                            {game.name}
                                        </h2>
                                    </div>
                                </Link>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}

export default Developed;
