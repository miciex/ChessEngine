function changeTurn(turn) {
  if (turn === "white") {
    return "black";
  } else if (turn === "black") {
    return "white";
  } else return;
}

export default changeTurn;
