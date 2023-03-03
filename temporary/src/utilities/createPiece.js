import King from "../components/Pieces/King";
import Pawn from "../components/Pieces/Pawn";
import Queen from "../components/Pieces/Queen";
import Knight from "../components/Pieces/Knight";
import Rook from "../components/Pieces/Rook";
import Bishop from "../components/Pieces/Bishop";
import SquareData from "../components/SquareData";

function createPiece(piece, x, y, color, howManyMoves = 0) {
  if (piece == "") return new SquareData(x, y);
  switch (piece) {
    case "Pawn":
      if (y > 0 && y < 7) {
        return new Pawn(x, y, color, howManyMoves);
      }
    case "Rook":
      return new Rook(x, y, color, howManyMoves);
    case "Bishop":
      return new Bishop(x, y, color);
    case "Queen":
      return new Queen(x, y, color);
    case "Knight":
      return new Knight(x, y, color);
    case "King":
      return new King(x, y, color, howManyMoves);
  }
}

export default createPiece;
