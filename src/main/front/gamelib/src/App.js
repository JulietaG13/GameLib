import './App.css';
import {BrowserRouter, Routes, Route} from "react-router-dom"
import ABM from "./Components/ManageVideogame/ManageVideogame";
import VideogameView from "./Components/VideogameView/VideogameView2";
import Home from "./Components/Home/Home";
import Library from "./Components/TheLibrary/Library";
import HeaderV2 from "./Components/Header/HeaderV2";
import Profile from "./Components/Profile/Profile";
import LoginRework from "./Components/LoginSignup/loginRework";
import RegisterRework from "./Components/LoginSignup/registerRework";
function App() {
    return <BrowserRouter>
        <div className={'min-h-[100vh]'}>
            <Routes>
                <Route path="/library" element={<Library/>}/>
                <Route path="/" element={<Home/>}/>
                <Route path="/Login" element={<LoginRework/>}/>
                <Route path="/register" element={<RegisterRework/>}/>
                <Route path="/addVideogame" element={<ABM type={"Add"}/>}/>
                <Route path="/editVideogame/:videogameID" element={<ABM type={"Edit"}/>}/>
                <Route path="/videogame/:videogameID" element={<VideogameView/>}/>
                <Route path="/profile/:username" element={<Profile/>}/>
            </Routes>
        </div>
    </BrowserRouter>
}

export default App;
