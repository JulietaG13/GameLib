import React from "react"
import '../LoginSignup/LoginSignup.css'
import {Link} from "react-router-dom";

//Home component
const Home = () => {
    return(
        <div className='container'>
            <div className='header'>
                <div className="text">{"Home"}</div>
                <div className="underline"></div>
            </div>
            <div className="submit-container">
                <button className={"submit"} type={"submit"}>
                    <Link to="/login" className={"link"}> Login </Link>
                </button>
                <button className={"submit"} type={"submit"} >
                    <Link to="/register" className={"link"}> Register </Link>
                </button>
            </div>
        </div>
    );
};
export default Home;


