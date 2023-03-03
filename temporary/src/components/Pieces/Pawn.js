import Piece from "../Piece";
import inBounds from "../../utilities/inBounds";
import absoluteValue from "../../utilities/absoluteValue";
import doMove from "../../utilities/doMove";
import moveUt from "../../utilities/moveUt";

class Pawn extends Piece {
  constructor(x, y, pieceColor, didMove = 0) {
    super(x, y, pieceColor, "Pawn");
    this.dr = pieceColor === "white" ? [-1, -2, -1, -1] : [1, 2, 1, 1];
    this.dc = [0, 0, 1, -1];
    this.didMove = didMove;
    this.points = 1;
  }

  //methond which finds possible moves for the clicked Pawn
  possibleMoves(board, lastMove) {
    //array for possible row directions
    let possibledr = [];
    //array for possible column directions
    let possibledc = [];
    //variables for possible row and column coordinates
    let Py, Px;
    for (let i = 0; i < this.dr.length; i++) {
      Py = this.y + this.dr[i];
      Px = this.x + this.dc[i];
      //checking if the new square wouldn't be out of the board range if so this move isn't possible
      if (inBounds(Px, Py) === false) continue;
      //checking if on the new square doesn't stand piece of the same color if so the piece isn't able to move there
      if (board[this.y][this.x].pieceColor === board[Py][Px].pieceColor)
        continue;
      //checking if piece ever moved if so it's unable to move 2 squares forward
      if (this.didMove > 0 && i === 1) continue;
      //checking if the square square in front of the pawn is empty for the move by 2 squares
      if (i === 1 && board[Py - this.dr[0]][Px].pieceColor !== "") continue;
      //checking for the moves forward if the new square is empty if not then it's not possible to make this move
      if (i < 2 && board[Py][Px].pieceColor !== "") continue;
      //checking for the takes moves if it on the new square there is a piece if so it is possible to take, because earlier we checked if there is a piece of the same color, so when it's not empty you can take
      if (i > 1 && board[Py][Px].pieceColor === "") continue;
      //checking if it is possible to do en pessant
      if (
        lastMove !== undefined &&
        i > 1 &&
        (lastMove.piece !== "Pawn" ||
          this.y !== lastMove.endY ||
          (i === 2 && lastMove.endX - 1 !== this.x) ||
          (i === 3 && lastMove.endX + 1 !== this.x) ||
          absoluteValue(lastMove.startY - lastMove.endY) == 2)
      )
        continue;
      //adding possible moves
      // debugger;
      possibledr.push(Py);
      possibledc.push(Px);
    }
    //returning array with squares to which pawn can move
    return [possibledr, possibledc];
  }

  move(x, y, board, moves, possibleMoves, promotionSquare, whichPromotion) {
    debugger;
    if (moveUt(possibleMoves, y, x) === false) return false;
    return doMove(this, x, y, board, moves, promotionSquare, whichPromotion);
  }
}

export default Pawn;
