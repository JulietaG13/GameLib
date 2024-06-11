import React, { useRef } from 'react';
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

// Adjusted GameItem styling
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


function GamesFromDB({ gamesFromDB, title }) {
    const scrollRef = useRef(null);


    return (
        <Container>
            <h2 className="font-schibsted text-[30px] text-black pt-10 pb-5 ">{title}</h2>
            <ScrollContainer ref={scrollRef}>
                {gamesFromDB.map((game) => (
                    <Link key={game.id} to={'/videogame/' + game.id}>
                        <GameItem> {/* Use the styled GameItem component */}
                            <img className={""}
                                src={game.background_image}
                                alt={game.name}
                            />
                            <h2 className={"p-1 flex justify-center"}>{game.name}</h2>
                        </GameItem>
                    </Link>
                ))}
            </ScrollContainer>
        </Container>
    );
}

export default GamesFromDB;
