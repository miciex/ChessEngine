import React from "react";
import "../css/Square.css";
import boardhandle from "./boardhandle";
import AllPromotion from "./AllPromotion";
import { useEffect } from "react";

export default function Square({ click, x, y, state, promotion }) {
  let data = null;
  let prom = state.promote[0]===(x-1) &&state.promote[1]===(y-1)
  let smt
  if(state.clickedSquare[0]!=='')
  smt = state.board[state.clickedSquare[1]][state.clickedSquare[0]].piece==='Pawn'
  return (
    <>
      <div
        className={"square " + state.board[y - 1][x - 1].pieceColor}
        onClick={() => {
          data = boardhandle(state, x - 1, y - 1, promotion);
          if (data !== null) click(data);
        }}
      >
        {state.board[y - 1][x - 1].piece}
      </div>
      {(prom===true&&smt ===true)? <AllPromotion state={state} click={click} x={x-1} y={y-1}/>:null}
    </>
  );
}

//<AllPromotion state={state} x={x} y={y} click={click} />