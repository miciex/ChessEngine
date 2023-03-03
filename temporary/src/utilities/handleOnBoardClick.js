import changeTurn from "./changeTurn";
import copy2dArray from "./copy2dArray";

function handleOnBoardClick(
  ifPlayable,
  board,
  moves,
  x,
  y,
  possibleMoves,
  whoseTurn,
  clickedSquare,
  whichPromotion = ''
) {
  debugger;
  if (ifPlayable === false) return;
  if (board[y][x].piece === "" && clickedSquare[0] === "") return;
  if (board[y][x].pieceColor !== whoseTurn && clickedSquare[0] === "") return;
  if (
    clickedSquare[0] !== "" &&
    board[clickedSquare[1]][clickedSquare[0]].piece === "Pawn" &&
    (y === 7 || y === 0) &&
    typeof whichPromotion !== "string" &&
    board[clickedSquare[1]][clickedSquare[0]].pieceColor === whoseTurn
  )
    return {
      promotion: -1,
      x: x,
      y: y,
    };
  let brd = copy2dArray(board);
  let mvs = [...moves];
  let moved = false;
  if (possibleMoves.length > 0) {
    moved = board[clickedSquare[1]][clickedSquare[0]].move(
      x,
      y,
      brd,
      mvs,
      possibleMoves,
      whichPromotion
    );

    whichPromotion = null;
  }

  if (moved !== false) {
    possibleMoves = [];
    whoseTurn = changeTurn(whoseTurn);
    return {
      possibleMoves: possibleMoves,
      whoseTurn: whoseTurn,
      moves: moved.moves,
      board: moved.board,
      lastMove: moved.lastMove,
      promotion: whichPromotion,
    };
  }

  if (
    possibleMoves.length === 0 ||
    (board[y][x].pieceColor ===
      board[clickedSquare[1]][clickedSquare[0]].pieceColor &&
      board[y][x].pieceColor === whoseTurn)
  ) {
    possibleMoves = [];
    possibleMoves = board[y][x].possibleMoves(brd);
    possibleMoves = board[y][x].afterCheck(possibleMoves, brd);
  }
  return {
    board: brd,
    possibleMoves: possibleMoves,
    promotion: whichPromotion,
  };
}

export default handleOnBoardClick;
