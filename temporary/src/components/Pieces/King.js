import inBounds from "../../utilities/inBounds";
import doMove from "../../utilities/doMove";
import moveUt from "../../utilities/moveUt";
import Piece from "../Piece";
import underAttack from "../../utilities/underAttack";
import absoluteValue from "../../utilities/absoluteValue";
import inBetweenPieces from "../../utilities/inBetweenPieces";

class King extends Piece {
  constructor(x, y, pieceColor, didMove = 0) {
    super(x, y, pieceColor, "King");
    this.dr = [1, 1, 1, 0, 0, -1, -1, -1, 0, 0, 2, -2];
    this.dc = [1, -1, 0, 1, -1, 0, 1, -1, -2, 2, 0, 0];
    this.didMove = didMove;
  }

  possibleMoves(board) {
    let Py, Px;
    let possibledc = [];
    let possibledr = [];
    for (let i = 0; i < this.dr.length; i++) {
      Py = this.y + this.dr[i];
      Px = this.x + this.dc[i];
      // let a = inBetweenPieces(board, this.x, this.y, Px, Py);
      // debugger;
      //checking if the new square wouldn't be out of the board range if so this move isn't possible
      if (inBounds(Px, Py) === false) continue;
      //checking if on the new square doesn't stand piece of the same color if so the piece isn't able to move there
      if (board[this.y][this.x].pieceColor === board[Py][Px].pieceColor)
        continue;
      //checking if it is possible to do any castles move

      if (
        (this.didMove > 0 ||
          underAttack(board, this.x, this.y, this.pieceColor, true) === true) &&
        i > 7
      )
        break;
      if (
        i === 8 &&
        (board[this.y][0].piece !== "Rook" ||
          underAttack(board, this.x, this.y, this.pieceColor, true) === true ||
          inBetweenPieces(board, this.x, this.y, Px, Py) > 0)
      )
        continue;
      if (
        i === 9 &&
        (board[this.y][7].piece !== "Rook" ||
          underAttack(board, this.y, this.x + 1, this.pieceColor, true) ===
            true ||
          inBetweenPieces(board, this.x, this.y, Px, Py) > 0)
      )
        continue;
      if (i > 9 && board[absoluteValue(this.y - 7)][this.x].piece !== "Rook")
        continue;
      if (
        i === 10 &&
        (underAttack(board, this.y + 1, this.x, this.pieceColor, true) ===
          true ||
          inBetweenPieces(board, this.x, this.y, Px, Py) > 0)
      )
        continue;
      if (
        i === 11 &&
        (underAttack(board, this.y - 1, true) === true ||
          inBetweenPieces(board, this.x, this.y, Px, Py) > 0)
      )
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

export default King;
