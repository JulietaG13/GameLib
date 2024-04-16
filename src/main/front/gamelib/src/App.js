import './App.css';
import Header from "./Components/Header/Header"
import {BrowserRouter, Routes, Route, Outlet} from "react-router-dom"
import Login from "./Components/LoginSignup/Login";
import {Register} from "./Components/LoginSignup/register";
import ABM from "./Components/ManageVideogame/ManageVideogame";

function App() {
    return <BrowserRouter>
        <Routes>
            <Route path="/Login" element={<Login/>}/>
            <Route path="/register" element={<Register/>}/>
            <Route path="/addVideogame" element={<ABM type={"Add"} id={null} />} />
            <Route path="/editVideogame/:videogameID" element={<ABM type={"Edit"} id={10} />} />
        </Routes>
    </BrowserRouter>
}
//return (
//    <div>
//        <Header/>
//        <Register/>
//    </div>
//);
export default App;
