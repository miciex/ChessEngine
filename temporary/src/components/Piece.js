import SquareData from "./SquareData";
import isThenChecked from "../utilities/isThenChecked";
import copy2dArray from "../utilities/copy2dArray";

class Piece extends SquareData {
  constructor(x, y, pieceColor = "", piece = "") {
    super(x, y, pieceColor, piece);
  }

  afterCheck(possibleMoves, board) {
    if (this.piece === "") return;
    let brd = copy2dArray(board);
    let possibledr = [];
    let possibledc = [];
    for (let i = 0; i < possibleMoves[0].length; i++) {
      if (
        isThenChecked(
          brd,
          this.x,
          this.y,
          possibleMoves[1][i],
          possibleMoves[0][i]
        )
      )
        continue;
      possibledr.push(possibleMoves[0][i]);
      possibledc.push(possibleMoves[1][i]);
    }
    return [possibledr, possibledc];
  }
}

export default Piece;
