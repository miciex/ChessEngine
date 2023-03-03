import React from "react";
import Square from "./Square";
import "../css/Row.css";

export default function Row({
  y,
  click,
  state,
}) {
  const x = [1, 2, 3, 4, 5, 6, 7, 8];
  let squares = x.map((i) => (
    <Square
      x={i}
      y={y}
      click={click}
      state={state}
      key={String(i) + String(y)}
    />
  ));
  return <div className="row">{squares}</div>;
}
