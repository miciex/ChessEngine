import Board from "../components/Board";
import React, { useEffect, useRef, useState } from "react";
import io from "socket.io-client";
import brd from "../img/chessBoard.png";
const socket = io.connect("http://localhost:5000");

function Game({ click, state}) {
  return (
    <div className="placement">
      <div className="imgplace">
        <img src={brd} alt="" />
        <Board
          click={click}
          state={state}
        />
      </div>
    </div>
  );
}

export default Game;
