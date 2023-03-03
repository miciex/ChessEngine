class Move {
  constructor(
    pieceColor = "",
    piece = "",
    startX = "",
    startY = "",
    endX = "",
    endY = "",
    didTaken = "",
    didMove = ""
  ) {
    this.pieceColor = pieceColor;
    this.piece = piece;
    this.startX = startX;
    this.startY = startY;
    this.endX = endX;
    this.endY = endY;
    this.didTaken = didTaken;
    this.howManyMoves = didMove;
  }
}

export default Move;
