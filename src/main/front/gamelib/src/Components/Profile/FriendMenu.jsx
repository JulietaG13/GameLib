import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

function FriendMenu() {
    const [friends, setFriends] = useState([]);
    const [friendRequests, setFriendRequests] = useState([]);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isRequestValid, setIsRequestValid] = useState(false);

    useEffect(() => {
        validateLogin();
    }, []);

    const fetchFriends = async () => {
        const userId = localStorage.getItem("id");
        axios.get(`http://localhost:4567/user/friends/get/${userId}`, {})
            .then((response) => {
                setFriends(response.data.friends);
                setIsRequestValid(true); // La solicitud fue exitosa, establecer isRequestValid a true
            })
            .catch(error => {
                console.error("Error fetching friends:", error);
                setIsRequestValid(false); // La solicitud falló, establecer isRequestValid a false
            });

        axios.get(`http://localhost:4567/user/friends/pending/get`, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then((response) => {
                setFriendRequests(response.data.pending_requests);
            })
            .catch(error => {
                console.error("Error fetching friend requests:", error);
            });
    };

    const handleAcceptRequest = (friendId) => {
        validateLogin()
        axios.put(`http://localhost:4567/user/friends/accept/${friendId}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                setFriendRequests(friendRequests.filter(request => request.id !== friendId));
                fetchFriends();
            })
            .catch(error => {
                console.error("Error accepting friend request:", error);
            });
    };

    const handleRejectRequest = (requestId) => {
        validateLogin()
        axios.put(`http://localhost:4567/user/friends/reject/${requestId}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        })
            .then(() => {
                setFriendRequests(friendRequests.filter(request => request.id !== requestId));
            })
            .catch(error => {
                console.error("Error rejecting friend request:", error);
            });
    };

    function validateLogin() {
        // Check if user is logged in using token
        axios.post('http://localhost:4567/tokenvalidation', {}, {
            headers: {
                'Content-Type': 'application/json',
                'token': localStorage.getItem('token')
            }
        }).then(() => {
            fetchFriends();
        }).catch(e => {
            console.error('Error:', e);
        });
    }


    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    if (!isRequestValid) {
        return null; // No renderiza el menú si el usuario no está logueado o la solicitud no es válida
    }

    return (
        <>
            <div className={`fixed top-4 ${isMenuOpen ? 'right-48' : 'right-0'} z-50 transform ${isMenuOpen ? 'translate-x-0' : 'translate-x-1/2'}`}>
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
                <h2 className='font-bold text-xl mb-4 mt-8'>Friend Requests</h2>
                {friendRequests.length === 0 ? (
                    <p>No friend requests found</p>
                ) : (
                    <ul>
                        {friendRequests.map(request => (
                            <li key={request.id} className='mb-2'>
                                <div className='flex justify-between items-center'>
                                    <span>{request.username}</span>
                                    <div className='flex space-x-2'>
                                        <button onClick={() => handleAcceptRequest(request.id)} className="bg-green-600 text-white py-1 px-2 rounded">
                                            Accept
                                        </button>
                                        <button onClick={() => handleRejectRequest(request.id)} className="bg-red-600 text-white py-1 px-2 rounded">
                                            Reject
                                        </button>
                                    </div>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </>
    );
}

export default FriendMenu;
