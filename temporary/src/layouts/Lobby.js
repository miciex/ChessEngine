import React from "react";
import Game from "./Game";
import { Link } from "react-router-dom";

function Lobby() {
  return (
    <div>
      <p>
        <Link to="/game">Game</Link>
      </p>
    </div>
  );
}

export default Lobby;
