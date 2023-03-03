import createPiece from "./createPiece";
import SquareData from "../components/SquareData";
import copy2dArray from "./copy2dArray";

function ChangePiecesSquare(piece, newX, newY, board) {
  if (piece === "") return;
  let brd = copy2dArray(board);
  if (piece.howManyMoves !== undefined)
    brd[newY][newX] = createPiece(
      piece.piece,
      newX,
      newY,
      piece.pieceColor,
      piece.howManyMoves
    );
  else brd[newY][newX] = createPiece(piece.piece, newX, newY, piece.pieceColor);
  brd[piece.y][piece.x] = new SquareData(piece.x, piece.y);
  return brd;
}

export default ChangePiecesSquare;
