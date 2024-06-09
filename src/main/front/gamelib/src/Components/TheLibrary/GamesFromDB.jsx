import React, { useRef } from 'react';
import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

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
    padding-bottom: 1rem; /* Espacio entre los elementos y la barra de desplazamiento */
    scrollbar-width: thin; /* Firefox */
    scrollbar-color: #ff8341 transparent; /* Firefox */
    &::-webkit-scrollbar {
        height: 16px; /* Altura de la barra de desplazamiento (aumentada) */
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


function GamesFromDB({ gamesFromDB, title }) {
    const scrollRef = useRef(null);

    useEffect(() => {
        console.log("gameListByGenres", gamesFromDB);
    }, [gamesFromDB]);

    return (
        <Container>
            <h2 className="font-schibsted text-[30px] text-black pt-10 pb-5">{title}</h2>
            <ScrollContainer ref={scrollRef}>
                {gamesFromDB.map((game) => (
                    <Link key={game.id} to={'/videogame/' + game.id}>
                        <div className="flex-shrink-0 w-[250px] bg-black p-1.5 rounded-lg hover:scale-105 ease-in-out duration-300 cursor-pointer shadow-md">
                            <img src={game.background_image} className="w-full h-[400px] rounded-xl object-cover" alt={game.name} />
                            <h2 className="text-white pt-1 text-lg font-bold truncate">
                                {game.name}
                            </h2>
                        </div>
                    </Link>
                ))}
            </ScrollContainer>
        </Container>
    );
}

export default GamesFromDB;
