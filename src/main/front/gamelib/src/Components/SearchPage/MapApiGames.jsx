import React, { useRef, useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styled, { keyframes } from 'styled-components';

const Container = styled.div`
    background-color: #e5e7eb;
    position: relative;
    padding: 1.25rem;
    border-radius: 0.75rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const ScrollContainer = styled.div`
    display: flex;
    overflow-x: auto;
    overflow-y: hidden;
    gap: 1.5rem;
    padding-bottom: 1rem;
    scrollbar-width: thin; /* Firefox */
    scrollbar-color: #ff8341 transparent; /* Firefox */
    &::-webkit-scrollbar {
        height: 16px; /* Altura de la barra de desplazamiento */
    }
    &::-webkit-scrollbar-track {
        background: transparent; /* Fondo de la pista transparente */
    }
    &::-webkit-scrollbar-thumb {
        background: #ff8341; /* Color de la barra de desplazamiento */
        border-radius: 10px; /* Bordes redondeados */
        border: none; /* Sin borde para que solo se vea la barra */
    }
    &::-webkit-scrollbar-thumb:hover {
        background: #ff6a00; /* Color de la barra al pasar el ratón */
    }
`;

const GameItem = styled.div`
    flex-shrink: 0;
    width: 250px;
    background-color: black;
    padding: 0.1rem;
    border-radius: 0.75rem; 
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); 
    transition: transform 0.3s ease-in-out; 

    &:hover {
        transform: translateY(-5px); // Lift up on hover instead of scaling
        box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
    }

    img {
        width: 100%;
        height: 400px;
        object-fit: cover; 
        border-radius: 0.5rem;  
    }

    h2 {
        color: white;
        margin-top: 1rem;
        font-size: 1.125rem;  
        font-weight: bold;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
`;

// Skeleton Loader styles
const shimmer = keyframes`
    0% {
        background-position: -1000px 0;
    }
    100% {
        background-position: 1000px 0;
    }
`;

const SkeletonContainer = styled.div`
    flex-shrink: 0;
    width: 250px;
    height: 450px; // Adjust to fit the image height plus some margin
    background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    animation: ${shimmer} 1.5s infinite;
    border-radius: 0.75rem;
`;

const SkeletonLoader = () => (
    <SkeletonContainer />
);

function MapApiGames({ gamesFromDB, title }) {
    const scrollRef = useRef(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        setTimeout(() => {
            setIsLoading(false);
        }, 300);
    }, []);

    return (
        <Container>
            <h2 className="font-schibsted text-[30px] text-black pt-10 pb-5">{title}</h2>
            <ScrollContainer ref={scrollRef}>
                {isLoading ? (
                    Array.from({ length: 5 }).map((_, index) => (
                        <SkeletonLoader key={index} />
                    ))
                ) : (
                    gamesFromDB.map((game) => (
                        <Link key={game.id} to={'/videogame/' + game.id}>
                            <GameItem>
                                <img
                                    src={game.cover_url}
                                    alt={game.name}
                                />
                                <h2 className="p-1 flex justify-center">{game.name}</h2>
                            </GameItem>
                        </Link>
                    ))
                )}
            </ScrollContainer>
        </Container>
    );
}

export default MapApiGames;
