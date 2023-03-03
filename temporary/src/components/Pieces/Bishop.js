import moveUt from "../../utilities/moveUt";
import Piece from "../Piece";
import doMove from "../../utilities/doMove";
import inBounds from "../../utilities/inBounds";

class Bishop extends Piece {
  constructor(x, y, pieceColor) {
    super(x, y, pieceColor, "Bishop");
    this.dr = [1, 2, 3, 4, 5, 6, 7];
    this.points = 3;
  }

  possibleMoves(board) {
    let possibledc = [];
    let possibledr = [];
    let Py, Px;
    let y = this.y;
    let x = this.x;
    let dr = this.dr;
    F: for (let i = 0; i < 4; i++) {
      for (let j = 0; j < 7; j++) {
        if (i == 0) {
          Py = y + dr[j];
          Px = x + dr[j];
        } else if (i == 1) {
          Py = y - dr[j];
          Px = x - dr[j];
        } else if (i == 2) {
          Py = y + dr[j];
          Px = x - dr[j];
        } else if (i == 3) {
          Py = y - dr[j];
          Px = x + dr[j];
        }
        //checking if the new square wouldn't be out of the board range if so this move isn't possible
        if (inBounds(Px, Py) === false) continue F;
        //checking if on the new square doesn't stand piece of the same color if so the piece isn't able to move there
        if (board[y][x].pieceColor === board[Py][Px].pieceColor) continue F;
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

export default Bishop;
