import './App.css';
import {BrowserRouter, Routes, Route, Outlet, useParams} from "react-router-dom"
import Login from "./Components/LoginSignup/Login";
import {Register} from "./Components/LoginSignup/register";
import ABM from "./Components/ManageVideogame/ManageVideogame";
import Home from "./Components/Home/Home";
import Header from "./Components/Header/Header";
import Library from "./Components/TheLibrary/Library";

function App() {
    return <BrowserRouter>
        <div className={'min-h-[100vh]'}>
            <Routes>
                <Route path="/library" element={<Library/>}/>
                <Route path="/" element={<Home/>}/>
                <Route path="/Login" element={<Login/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/addVideogame" element={<ABM type={"Add"}/>}/>
                <Route path="/editVideogame/:videogameID" element={<ABM type={"Edit"}/>}/>
            </Routes>
        </div>
    </BrowserRouter>
}

export default App;
