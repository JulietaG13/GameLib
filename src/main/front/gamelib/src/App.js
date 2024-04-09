import './App.css';
import Header from "./Components/Header/Header"
import {BrowserRouter, Routes, Route, Outlet} from "react-router-dom"
import Login from "./Components/LoginSignup/Login";
import Register from "./Components/LoginSignup/register";
function App() {
    return <BrowserRouter>
        <Routes>
            <Route path="/Login" element={<Login/>}/>
            <Route path="/register" element={<Register/>}/>
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
