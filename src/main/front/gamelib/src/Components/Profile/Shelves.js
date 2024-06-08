import axios from "axios";
import React, { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

function Shelves({ username }) {
    const [shelves, setShelves] = useState([]);
    const [selectedGame, setSelectedGame] = useState(null);
    const [selectedShelf, setSelectedShelf] = useState(null);
    const contextMenuRef = useRef(null);

    const handleContextMenu = (event, game, shelf) => {
        event.preventDefault();

        // Position the context menu near the mouse click
        contextMenuRef.current.style.display = 'block';
        contextMenuRef.current.style.left = `${event.pageX}px`;
        contextMenuRef.current.style.top = `${event.pageY}px`;

        // Store the selected game and shelf in state
        setSelectedGame(game);
        setSelectedShelf(shelf);
    };

    useEffect(() => {
        axios.get(`http://localhost:4567/shelf/get/user/${username}/100`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(r => setShelves(r.data))
            .catch(error => console.error("Error fetching shelves:", error));
    }, [username]);

    const handleClickOutside = (event) => {
        if (contextMenuRef.current && !contextMenuRef.current.contains(event.target)) {
            contextMenuRef.current.style.display = 'none';
        }
    };

    useEffect(() => {
        document.addEventListener('click', handleClickOutside);
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, []);

    const handleRemoveGame = () => {
        if (selectedGame && selectedShelf) {
            console.log("Remove game:", selectedGame, "from shelf:", selectedShelf);
            axios.post(`http://localhost:4567/shelf/remove/${selectedGame.shelf_id}/${selectedGame.id}/`, {}, {
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token')
                }
            }
            ).then(r => {

            }).catch(
                error => console.error("Error removing game from shelf:", error)
            )
        }
        contextMenuRef.current.style.display = 'none';
    };

    return (
        <div>
            {shelves.length === 0 ? (
                <div className="pl-16">
                    <h2 className="font-bold text-[25px] text-black pt-3 pb-5 pl-5">You have no shelves</h2>
                </div>
            ) : (
                shelves.map((shelf) => (
                    <div key={shelf.id} className={"pl-16"}>
                        <div className={'bg-white'} key={shelf.id}>
                            <h2 className={'font-bold text-[30px] text-black pt-10 pb-5'}> {shelf.name}</h2>
                            <div className={'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-6'}>
                                {shelf.games.map((game) => (
                                    <div
                                        onContextMenu={(event) => handleContextMenu(event, game, shelf)}
                                        key={game.id}
                                        className={'relative'}
                                    >
                                        <Link to={'/videogame/' + game.id}>
                                            <div
                                                className={'bg-[#ff8341] p-1.5 rounded-lg hover:scale-110 ease-in-out duration-300 cursor-pointer w-[280px] h-[350px]'}
                                            >
                                                <img src={game.background_image}
                                                     className={'w-full h-[260px] rounded-xl object-cover'} alt={""} />
                                                <h2 className={'text-white pt-1 text-lg font-bold truncate'}>
                                                    {game.name}
                                                </h2>
                                            </div>
                                        </Link>
                                    </div>
                                ))}
                                {/* Context Menu */}
                                <div ref={contextMenuRef} className="absolute bg-white border shadow-lg p-2 hidden">
                                    <button onClick={handleRemoveGame}>Remove game</button>
                                </div>
                            </div>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}

export default Shelves;
