import Login from "./Login";
import Register from "./Register";
import HomePage from "./HomePage";
import { Route, Routes } from "react-router-dom";
import Game from "./Game";
import Lobby from "./Lobby";

function Main({ click, state}) {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/game"
        element={
          <Game
            click={click}
            state={state}
          />
        }
      />
      <Route path="/lobby" element={<Lobby />} />
    </Routes>
  );
}

export default Main;
