import inBounds from "../../utilities/inBounds";
import moveUt from "../../utilities/moveUt";
import Piece from "../Piece";
import doMove from "../../utilities/doMove";

class Rook extends Piece {
  constructor(x, y, pieceColor, didMove = 0) {
    super(x, y, pieceColor, "Rook");
    this.dr = [1, 2, 3, 4, 5, 6, 7];
    this.didMove = didMove;
    this.points = 5;
  }

  possibleMoves(board, lastMove) {
    let possibledc = [];
    let possibledr = [];
    let Py, Px;
    F: for (let i = 0; i < 4; i++) {
      for (let j = 0; j < this.dr.length; j++) {
        if (i < 2) {
          Py = this.y;
          if (i == 0) Px = this.x + this.dr[j];
          else Px = this.x - this.dr[j];
        } else if (i > 1) {
          if (i == 2) Py = this.y + this.dr[j];
          else Py = this.y - this.dr[j];
          Px = this.x;
        }
        //checking if the new square wouldn't be out of the board range if so this move isn't possible
        if (inBounds(Px, Py) === false) continue F;
        //checking if on the new square doesn't stand piece of the same color if so the piece isn't able to move there
        if (board[this.y][this.x].pieceColor === board[Py][Px].pieceColor)
          continue F;
        possibledr.push(Py);
        possibledc.push(Px);
        if (board[Py][Px].pieceColor !== "") continue F;
      }
    }
    return [possibledr, possibledc];
  }

  move(x, y, board, moves, possibleMoves) {
    if (moveUt(possibleMoves, y, x) === false) return false;
    return doMove(this, x, y, board, moves);
  }
}

export default Rook;
