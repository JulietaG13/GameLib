import React, { useRef } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import user_icon from '../Assets/user-icon.png';

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

const ProfileImage = styled.img`
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;

`;

const UserName = styled.p`
  text-align: center;
  margin-top: 0.5rem;
  font-size: 1rem;
  color: black;

`;

// Adjusted UserCard for better layout
const UserCard = styled(Link)`
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  width: 120px;   // Added width to prevent wrapping
`;

function MapUsers({ users }) {
    const scrollRef = useRef(null);

    console.log(users);
    return (
        <Container>
            <h2 className="font-schibsted text-[30px] text-black pb-5">Users</h2>
            <ScrollContainer ref={scrollRef}>
                {users.map((user) => (
                    <UserCard key={user.id} to={`/profile/${user.username}`}>
                        <div className={"border-black border-2 rounded-full bg-[#bfbfbf]"}>
                            <ProfileImage src={user.pfp || user_icon} alt={user.name} />
                        </div>
                        <UserName>{user.username}</UserName>
                    </UserCard>
                ))}
            </ScrollContainer>
        </Container>
    );
}


export default MapUsers;
