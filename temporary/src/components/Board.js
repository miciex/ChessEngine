import { useState } from "react";
import React from "react";
import Row from "./Row";
import "../css/Board.css";

export default function Board({
  click,
  state ,
}) {
  const [underPromote, setUnderPromote] = useState({ x: null, y: null });
  const y = [1, 2, 3, 4, 5, 6, 7, 8];
  let rows = y.map((row) => (
    <Row
      y={row}
      click={click}
      state={state}
      key={row}
    />
  ));
  return <div className="board">{rows}</div>;
}
