import React, { useState } from "react";
import "../css/Square.css";
import handleOnBoardClick from "../utilities/handleOnBoardClick";
import AllPromotion from "./AllPromotion";

export default function Square({
  click,
  x,
  y,
  state,
  promote,
  promotion,
  setUnderPromote,
  underPromote,
}) {
  let data = null;
  return (
    <>
      <div
        className={"square " + state.board[y - 1][x - 1].pieceColor}
        onClick={() => {
          data = handleOnBoardClick(
            state.ifPlayable,
            state.board,
            state.moves,
            x - 1,
            y - 1,
            state.possibleMoves,
            state.whoseTurn,
            state.clickedSquare,
            promotion
          );

          if (data !== undefined && data.moves !== undefined) {
            click(
              x - 1,
              y - 1,
              data.possibleMoves,
              data.board,
              data.moves,
              data.lastMove,
              data.whoseTurn
            );
          } else if (data.possibleMoves !== undefined) {
            click(x - 1, y - 1, data.possibleMoves);
          } else click(x - 1, y - 1);

          promote(data.promotion !== undefined ? data.promotion : null);
          debugger;
          if (data.promotion !== undefined && data.promotion !== null) {
            setUnderPromote({
              x: data.x,
              y: data.y,
            });
          } else if (state.promotion === null) {
            setUnderPromote({
              x: null,
              y: null,
            });
          }
        }}
      >
        {state.board[y - 1][x - 1].piece}
      </div>
      {underPromote.x !== null &&
        underPromote.x === x - 1 &&
        underPromote.y === y - 1 && (
          <AllPromotion
            x={x - 1}
            y={y - 1}
            promote={promote}
            promotion={promotion}
            click={click}
            state={state}
            uX={underPromote.x}
            uY={underPromote.y}
          />
        )}
    </>
  );
}
