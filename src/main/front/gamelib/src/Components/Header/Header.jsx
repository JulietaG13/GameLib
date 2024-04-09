import React, {useState} from "react";
import './Header.css';

import gamelibLogo from '../Assets/gamelib-logo.jpg';
import user_icon from '../Assets/user-icon.png';

function Header() {
    return (
        <navbar className="viewsContainer">
            <div className="logo">
                <img src={gamelibLogo} alt={"GameLib logo"}/>
                <h2>GameLib</h2>
            </div>
            <div className="goTo">
                <h2><a href="https://youtu.be/dQw4w9WgXcQ?si=ZImQDCkmnZI0wC_z" >The Library</a></h2>
            </div>
            <div className="goTo">
                <h2><a href="https://youtu.be/dQw4w9WgXcQ?si=ZImQDCkmnZI0wC_z">My Gameshelf</a></h2>
            </div>
            <div className="goTo">
                <h2><a href="https://youtu.be/dQw4w9WgXcQ?si=ZImQDCkmnZI0wC_z">Discover</a></h2>
            </div>
            <div className="user">
                <h2>Username</h2>
                <img src={user_icon} alt={"user icon"}/>
            </div>
        </navbar>
    );
}

export default Header;