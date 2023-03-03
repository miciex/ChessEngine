import moveUt from "../../utilities/moveUt";
import inBounds from "../../utilities/inBounds";
import Piece from "../Piece";
import doMove from "../../utilities/doMove";

class Knight extends Piece {
  constructor(x, y, pieceColor) {
    super(x, y, pieceColor, "Knight");
    this.dr = [2, 2, -2, -2, 1, -1, 1, -1];
    this.dc = [1, -1, 1, -1, 2, 2, -2, -2];
    this.points = 3;
  }

  possibleMoves(board) {
    let possibledc = [];
    let possibledr = [];
    let Py, Px;
    for (let i = 0; i < this.dr.length; i++) {
      Py = this.y + this.dr[i];
      Px = this.x + this.dc[i];
      if (inBounds(Px, Py) === false) continue;
      //checking if on the new square doesn't stand piece of the same color if so the piece isn't able to move there
      if (board[this.y][this.x].pieceColor === board[Py][Px].pieceColor)
        continue;
      possibledr.push(Py);
      possibledc.push(Px);
    }
    return [possibledr, possibledc];
  }

  move(x, y, board, moves, possibleMoves) {
    if (moveUt(possibleMoves, y, x) === false) return false;
    return doMove(this, x, y, board, moves);
  }
}

export default Knight;
