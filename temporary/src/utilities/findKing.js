function findKing(board, color) {
  for (let i = 0; i < board.length; i++) {
    for (let j = 0; j < board[i].length; j++) {
      if (board[i][j].piece === "King" && board[i][j].pieceColor === color)
        return [i, j];
    }
  }
}

export default findKing;
