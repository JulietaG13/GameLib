import './App.css';
import {BrowserRouter, Routes, Route} from "react-router-dom"
import ABM from "./Components/ManageVideogame/ManageVideogame";
import VideogameView from "./Components/VideogameView/VideogameView";
import Library from "./Components/TheLibrary/Library";
import Profile from "./Components/Profile/Profile";
import Login from "./Components/LoginSignup/Login";
import RegisterRework from "./Components/LoginSignup/Register";
import EditProfile from "./Components/Profile/EditProfile";


function App() {
    return <BrowserRouter>
        <div className={'min-h-[100vh]'}>
            <Routes>
                <Route path="/" element={<Library/>}/>
                <Route path="/Login" element={<Login/>}/>
                <Route path="/register" element={<RegisterRework/>}/>
                <Route path="/addVideogame" element={<ABM type={"Add"}/>}/>
                <Route path="/editVideogame/:videogameID" element={<ABM type={"Edit"}/>}/>
                <Route path="/videogame/:videogameID" element={<VideogameView/>}/>
                <Route path="/profile/:username" element={<Profile/>}/>
                <Route path="/profile/:username/edit" element={<EditProfile/>}/>
            </Routes>
        </div>
    </BrowserRouter>
}

export default App;
