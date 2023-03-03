import moveUt from "../../utilities/moveUt";
import doMove from "../../utilities/doMove";
import inBounds from "../../utilities/inBounds";
import Piece from "../Piece";

class Queen extends Piece {
  constructor(x, y, pieceColor) {
    super(x, y, pieceColor, "Queen");
    this.dr = [1, 2, 3, 4, 5, 6, 7];
    this.points = 9;
  }

  possibleMoves(board) {
    let possibledc = [];
    let possibledr = [];
    let Py, Px;
    F: for (let i = 0; i < 8; i++) {
      for (let j = 0; j < 7; j++) {
        if (i == 0) {
          Py = this.y + this.dr[j];
          Px = this.x + this.dr[j];
        } else if (i == 1) {
          Py = this.y - this.dr[j];
          Px = this.x - this.dr[j];
        } else if (i == 2) {
          Py = this.y + this.dr[j];
          Px = this.x - this.dr[j];
        } else if (i == 3) {
          Py = this.y - this.dr[j];
          Px = this.x + this.dr[j];
        } else if (i == 4) {
          Py = this.y;
          Px = this.x + this.dr[j];
        } else if (i == 5) {
          Py = this.y;
          Px = this.x - this.dr[j];
        } else if (i == 6) {
          Py = this.y - this.dr[j];
          Px = this.x;
        } else if (i == 7) {
          Py = this.y + this.dr[j];
          Px = this.x;
        }
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

export default Queen;
