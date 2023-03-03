function moveUt(possibleMoves, Ny, Nx) {
  let possibledr = possibleMoves[0];
  let possibledc = possibleMoves[1];
  for (let i = 0; i < possibledr.length; i++) {
    if (Ny == possibledr[i] && Nx == possibledc[i]) {
      return true;
    }
  }
  return false;
}

export default moveUt;
