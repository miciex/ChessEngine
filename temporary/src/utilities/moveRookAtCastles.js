import copy2dArray from "./copy2dArray";
import ChangePiecesSquare from "./changePiecesSquare";
import absoluteValue from "./absoluteValue";

function moveRookAtCastles(board, lastMove) {
  let brd = copy2dArray(board);
  if (lastMove.endX - lastMove.startX === 2) {
    brd = ChangePiecesSquare(
      brd[lastMove.startY][7],
      lastMove.startX + 1,
      lastMove.startY,
      brd
    );
  } else if (lastMove.endX - lastMove.startX === -2) {
    brd = ChangePiecesSquare(
      board[lastMove.startY][0],
      lastMove.startX - 1,
      lastMove.startY,
      brd
    );
  } else if (lastMove.endY - lastMove.startY === 2) {
    brd = ChangePiecesSquare(
      board[absoluteValue(lastMove.startY - 7)][lastMove.startX],
      lastMove.startX,
      absoluteValue(lastMove.startY - 6),
      brd
    );
  } else if (lastMove.endY - lastMove.startY === -2) {
    brd = ChangePiecesSquare(
      board[absoluteValue(lastMove.startY - 7)][lastMove.startX],
      lastMove.startX,
      absoluteValue(lastMove.startY - 6),
      brd
    );
  }
  return brd;
}

export default moveRookAtCastles;
