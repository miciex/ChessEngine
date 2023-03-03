import changeTurn from "../utilities/changeTurn";
import copy2dArray from "../utilities/copy2dArray";

function boardhandle(currData, x, y, whichPromotion = '') {
  let {
    ifPlayable,
    board,
    moves,
    possibleMoves,
    whoseTurn,
    lastMove,
    clickedSquare,promote
  } = currData;
  if (ifPlayable === false) return null;
  if (board[y][x].pieceColor !== whoseTurn && clickedSquare[0] === "")
    return null;
  if (clickedSquare === [x, y]) return null;
  //if the clicked piece is the same color as the turn of the pieces which turn it is return changed coordinates of chosen piece, and compute it's possible moves
  let brd = copy2dArray(board);
  // let promote = ["", ""];
  if (board[y][x].pieceColor === whoseTurn) {
    possibleMoves = [];
    possibleMoves = board[y][x].possibleMoves(brd);
    possibleMoves = board[y][x].afterCheck(possibleMoves, brd);
    return {
      clickedSquare: [x, y],
      possibleMoves,
      board,
      moves,
      lastMove,
      whoseTurn,
      ifPlayable,
      promote,
    };
  }

  //don't change anything if there are no possible moves for the chosen piece
  if (possibleMoves.length < 1) {
    return null;
  }

  if (
    board[clickedSquare[1]][clickedSquare[0]].piece === "Pawn" &&
    (y === 7 || y === 0)&&whichPromotion<2
  ) {
    return {
      clickedSquare,
      possibleMoves,
      board,
      moves,
      lastMove,
      whoseTurn,
      ifPlayable,
      promote: [x,y],
    };
  }
  
  let moved;
  debugger
  if (possibleMoves.length > 0) {
    moved = board[clickedSquare[1]][clickedSquare[0]].move(
      x,
      y,
      brd,
      moves,
      possibleMoves,
      (whichPromotion===undefined?promote:whichPromotion)
    );
  }

  if (moved !== undefined) {
    whoseTurn = changeTurn(whoseTurn);
    return {
      clickedSquare: ["", ""],
      possibleMoves: [],
      board: moved.board,
      moves: moved.moves,
      lastMove: moved.lastMove,
      whoseTurn,
      ifPlayable,
      promote,
    };
  }
}

export default boardhandle;

// if (
//   board[clickedSquare[1]][clickedSquare[0]].piece === "Pawn" &&
//   (y === 7 || y === 0)
// )
