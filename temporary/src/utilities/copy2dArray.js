import createPiece from "./createPiece";

function copy2dArray(currentArray) {
  return currentArray.map((arr) => {
    return arr.map((elem) => {
      return createPiece(
        elem.piece,
        elem.x,
        elem.y,
        elem.pieceColor,
        elem.didMove ?? 0
      );
    });
  });
}

export default copy2dArray;
