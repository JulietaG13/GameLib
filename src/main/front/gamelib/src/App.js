import './App.css';
import {BrowserRouter, Routes, Route, Outlet} from "react-router-dom"
import Login from "./Components/LoginSignup/Login";
import {Register} from "./Components/LoginSignup/register";
import ABM from "./Components/ManageVideogame/ManageVideogame";
import Home from "./Components/Home/Home";
import Header from "./Components/Header/Header";

function App() {
    return <BrowserRouter>
        <Header></Header>
        <Routes>
            <Route path="/" element={<Home/>}/>
            <Route path="/Login" element={<Login/>}/>
            <Route path="/register" element={<Register/>}/>
            <Route path="/addVideogame" element={<ABM type={"Add"} id={null} />} />
            <Route path="/editVideogame/:videogameID" element={<ABM type={"Edit"} id={10} />} />
        </Routes>
    </BrowserRouter>
}

export default App;
