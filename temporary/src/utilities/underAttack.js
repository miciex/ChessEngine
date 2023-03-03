import copy2dArray from "./copy2dArray";

function underAttack(board, x, y, color, king = false) {
  let Pm;
  let brd = copy2dArray(board);
  for (let i = 0; i < brd.length; i++) {
    for (let j = 0; j < brd.length; j++) {
      if (brd[i][j].piece === "") continue;
      if (brd[i][j].pieceColor === color) continue;
      if (brd[i][j].piece === "King" && king === true) continue;
      Pm = brd[i][j].possibleMoves(brd);
      for (let k = 0; k < Pm[0].length; k++) {
        if (Pm[0][k] == y && Pm[1][k] == x) {
          return true;
        }
      }
    }
  }
  return false;
}

export default underAttack;
