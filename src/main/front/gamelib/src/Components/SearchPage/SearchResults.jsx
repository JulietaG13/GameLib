import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useLocation } from 'react-router-dom';
import Header from "../Header/Header";
import GamesFromDB from "../TheLibrary/GamesFromDB";
import MapUsers from "./MapUsers";
import MapApiGames from "./MapApiGames";

function useQuery() {
    return new URLSearchParams(useLocation().search);
}

const SearchResults = () => {
    const query = useQuery();
    const searchQuery = query.get('query');
    const [games, setGames] = useState([]);
    const [apiGames, setApiGames] = useState([]);
    const [users, setUsers] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        if (searchQuery) {
            fetchSearchResults().then(r =>{} );
        }
    }, [searchQuery]);

    const fetchSearchResults = async () => {
        try {
            const response = await axios.get(`http://localhost:4567/common/search/all/${searchQuery}`, {}, {});
            const apiResponse = await axios.get(`http://localhost:4567/common/search/api/${searchQuery}`, {}, {});
            setGames(response.data.games);
            setUsers(response.data.users);
            setApiGames(apiResponse.data);
            console.log(apiResponse.data);

        } catch (error) {
            console.error('Error fetching search results:', error);
            setError('Failed to fetch search results');
        }
    };

    return (
        <div>
            <Header/>
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-4">Search Results for "{searchQuery}"</h1>

                {error && <div className="bg-red-500 text-white p-4 rounded-md mb-4">{error}</div>}

                <div className="mb-8">
                    {games.length === 0 ? (
                        <p>No games found inside GameLib</p>
                    ) : (
                        <GamesFromDB gamesFromDB={games} title="Games"/>
                    )}
                </div>

                <div>
                    {users.length === 0 ? (
                        <p>No users found </p>
                    ) : (
                        <MapUsers users={users}/>
                    )}
                </div>

                <div className="mb-8">
                    {apiGames.length === 0 ? (
                        <p>No games found</p>
                    ) : (
                        <MapApiGames gamesFromDB={apiGames} title="You may be looking for"/>
                    )}
                </div>


            </div>
        </div>
    );
};

export default SearchResults;
