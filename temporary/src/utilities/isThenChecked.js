import changePiecesSquare from "./changePiecesSquare";
import findKing from "./findKing";
import underAttack from "./underAttack";
import copy2dArray from "./copy2dArray";

function isThenChecked(board, Sx, Sy, Px, Py) {
  let King;
  if (board[Sy][Sx].piece === "") return;
  let brd = copy2dArray(board);
  brd = changePiecesSquare(brd[Sy][Sx], Px, Py, brd);
  King = findKing(brd, brd[Py][Px].pieceColor);
  return underAttack(brd, King[1], King[0], brd[Py][Px].pieceColor);
}

export default isThenChecked;
