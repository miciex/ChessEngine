import createPiece from "./createPiece";
import SquareData from "../components/SquareData";
import Move from "./Move";
import Last2Moves from "./Last2Moves";
import moveRookAtCastles from "./moveRookAtCastles";

function doMove(piece, x, y, board, allmoves, whichPromotion) {
  let brd = [...board];
  let moves = allmoves;
  //adding this move to the variable Moves
  
  if (piece.pieceColor === "white") {
    moves.push(new Last2Moves());
  }
  moves[moves.length - 1][piece.pieceColor] = new Move(
    piece.pieceColor,
    piece.piece,
    piece.x,
    piece.y,
    x,
    y,
    board[y][x].value,
    piece.didMove !== undefined ? piece.didMove : ""
  );
  let lastMove = moves[moves.length - 1][piece.pieceColor];
  //Moving the pawn and underpromoting it if it went to the last row
  debugger;
  if (whichPromotion.length>2) {
    brd[y][x] = createPiece(whichPromotion, x, y, piece.pieceColor);
    
    return{ board: brd, moves: moves, lastMove: lastMove }
  } else {
    brd[y][x] = createPiece(
      piece.piece,
      x,
      y,
      piece.pieceColor,
      piece.howManyMoves+=1
    );
  }
  //Moving the piece if it is not promoting
  brd[piece.y][piece.x] = new SquareData(x, y);
  if (piece.piece === "King") {
    brd = moveRookAtCastles(brd, lastMove);
  }
  brd[piece.y][piece.x] = new SquareData(x, y);
  //Check if en pessant happened if it did take out the pawn of the board which was taken
  if (whichPromotion === "")
    return { board: brd, moves: moves, lastMove: lastMove };
  if (piece.piece !== "Pawn")
    return { board: brd, moves: moves, lastMove: lastMove };
  if (board[y][x].piece !== "")
    return { board: brd, moves: moves, lastMove: lastMove };
  if (piece.pieceColor === "white") board[x][y + 1] = new SquareData(x, y + 1);
  if (piece.pieceColor === "black") board[x][y + 1] = new SquareData(x, y - 1);
  moves[moves.length - 1][piece.pieceColor].didTaken = "Pawn";
  return { board: brd, moves: moves, lastMove: lastMove };
}

export default doMove;
