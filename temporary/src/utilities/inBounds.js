function inBounds(x, y) {
  if (x <= 7 && x >= 0 && y >= 0 && y <= 7) return true;
  else return false;
}

export default inBounds;
