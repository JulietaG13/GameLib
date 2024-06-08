import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

function FriendMenu() {
    const [friends, setFriends] = useState([]);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const loggedInUsername = localStorage.getItem('username');

    useEffect(() => {
        const fetchFriends = async () => {
            try {
                const userId = localStorage.getItem("id");
                const response = await axios.get(`http://localhost:4567/user/friends/get/${userId}`);
                setFriends(response.data.friends);
            } catch (error) {
                console.error("Error fetching friends:", error);
            }
        };

        fetchFriends();
    }, []);

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    if (!loggedInUsername) {
        return null; // No renderiza el menú si el usuario no está logueado
    }

    return (
        <>
            <div className={`fixed top-4 ${isMenuOpen ? 'right-32' : 'right-0'} z-50 transform ${isMenuOpen ? 'translate-x-0' : 'translate-x-1/2'}`}>
                <button onClick={toggleMenu} className="bg-gray-600 text-white py-2 px-4 rounded-full">
                    ☰
                </button>
            </div>
            <div className={`fixed top-0 right-0 h-full bg-gray-100 p-4 shadow-lg transition-transform duration-300 z-50 ${isMenuOpen ? 'translate-x-0' : 'translate-x-full'}`}>
                <h2 className='font-bold text-xl mb-4'>Friends</h2>
                {friends.length === 0 ? (
                    <p>No friends found</p>
                ) : (
                    <ul>
                        {friends.map(friend => (
                            <li key={friend.username} className='mb-2'>
                                <Link to={`/profile/${friend.username}`} className='text-blue-600 block p-2 hover:bg-gray-100'>
                                    {friend.username}
                                </Link>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </>
    );
}

export default FriendMenu;
