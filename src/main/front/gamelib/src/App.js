import './App.css';
import {BrowserRouter, Routes, Route} from "react-router-dom"
import Login from "./Components/LoginSignup/Login";
import {Register} from "./Components/LoginSignup/register";
import ABM from "./Components/ManageVideogame/ManageVideogame";
import VideogameView from "./Components/VideogameView/VideogameView";
import Home from "./Components/Home/Home";
import Library from "./Components/TheLibrary/Library";
import HeaderV2 from "./Components/Header/HeaderV2";

function App() {
    return <BrowserRouter>
        <div className={'min-h-[100vh]'}>
            <HeaderV2/>
            <Routes>
                <Route path="/library" element={<Library/>}/>
                <Route path="/" element={<Home/>}/>
                <Route path="/Login" element={<Login/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/addVideogame" element={<ABM type={"Add"}/>}/>
                <Route path="/editVideogame/:videogameID" element={<ABM type={"Edit"}/>}/>
                <Route path="/videogame/:videogameID" element={<VideogameView/>}/>
            </Routes>
        </div>
    </BrowserRouter>
}

export default App;
