import "../css/App.css";
import { BrowserRouter as Router } from "react-router-dom";
import React, { useState } from "react";
import CreateBoard from "../components/CreateBoard";
import Main from "./Main";

function App() {
  const [state, setState] = useState({
    board: CreateBoard(),
    clickedSquare: ["", ""],
    moves: [],
    lastMove: null,
    whoseTurn: "white",
    ifPlayable: true,
    possibleMoves: [],
    promote: ["", ""],
  });

  const handleOnClick = (data) => {
    setState(data);
  };

  return (
    <Router basename={process.env.PUBLIC_URL}>
      <Main
        click={handleOnClick}
        state={state}
      />
    </Router>
  );
}

export default App;
