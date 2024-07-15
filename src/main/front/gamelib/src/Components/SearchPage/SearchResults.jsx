import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import Header from "../Header/Header";
import GamesFromDB from "../TheLibrary/GamesFromDB";
import MapUsers from "./MapUsers";
import MapApiGames from "./MapApiGames";

function useQuery() {
    return new URLSearchParams(useLocation().search);
}

const Skeleton = () => (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {[...Array(5)].map((_, index) => (
            <div key={index} className="animate-pulse flex flex-col items-center mb-5 mt-5">
                <div className="w-[250px] h-[350px] bg-gray-300 rounded mb-2"></div>
                <div className="w-32 h-4 bg-gray-300 rounded"></div>
            </div>
        ))}
    </div>
);
const SearchResults = () => {
    const query = useQuery();
    const searchQuery = query.get('query');
    const [games, setGames] = useState([]);
    const [taggedGames, setTaggedGames] = useState([]);
    const [apiGames, setApiGames] = useState([]);
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (searchQuery) {
            fetchSearchResults();
        }
    }, [searchQuery]);

    const fetchSearchResults = async () => {
        setIsLoading(true);
        try {
            const apiResponse = await axios.get(`http://localhost:4567/common/search/api/${searchQuery}`, {}, {});
            setApiGames(apiResponse.data);
        } catch (error) {
            setApiGames([])
        }
        try {
            const response = await axios.get(`http://localhost:4567/common/search/all/${searchQuery}`, {}, {});
            setError('')
            setGames(response.data.games);
            setTaggedGames(response.data.taggedGames);
            setUsers(response.data.users);
        } catch (error) {
            console.error('Error fetching from gamelib:', error);
            setError('Failed to fetch search results');
            setGames([])
            setTaggedGames([])
            setUsers([])
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            <Header />
            <div className="container mx-auto p-4">
                <h1 className="text-4xl font-bold font-exo mb-4">Search Results for "{searchQuery}"</h1>

                {error && <div className="bg-red-500 text-white p-4 rounded-md mb-4">{error}</div>}

                <div className="mb-2">
                    {isLoading ? (
                        <Skeleton/>
                    ) : games.length === 0 && taggedGames.length === 0 ? (
                        <p className={"text-2xl font-bold italic p-5"}>No games found inside GameLib</p>
                    ) : games.length !== 0 && (
                        <GamesFromDB gamesFromDB={games} title="Games"/>
                    ) || taggedGames.length !== 0 && (
                        <GamesFromDB gamesFromDB={taggedGames} title={`Games tagged with: ${searchQuery}`}/>
                    )
                    }
                </div>

                <div className="mb-2">
                    {isLoading ? (
                        <Skeleton/>
                    ) : users.length === 0 ? (
                        <p className={"text-2xl font-bold italic p-5"}>No users found</p>
                    ) : (
                        <MapUsers users={users}/>
                    )}
                </div>

                <div className="mb-2">
                    {isLoading ? (
                        <Skeleton/>
                    ) : apiGames.length === 0 ? (
                        <p className={"text-2xl font-bold italic p-5"}>No games found outside GameLib</p>
                    ) : (
                        <MapApiGames gamesFromDB={apiGames} title="You may be looking for"/>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SearchResults;
