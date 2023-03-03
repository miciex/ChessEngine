import Promote from "./Promote";

function AllPromotion({ x, y, click, state }) {
  let a = [];
  let data;
  const pieces = ["Queen", "Rook", "Knight", "Bishop"];
  for (let i = 0; i < 4; i++) {
    if (y === 0) {
      a.push(
        <Promote
          x={x}
          y={y + i}
          value={pieces[i]}
          key={pieces[i]}
          state={state}
          click={click}
        />
      );
    } else if (y === 7) {
      a.push(
        <Promote
          x={x}
          y={y - i}
          value={pieces[i]}
          key={pieces[i]}
          state={state}
          click={click}
        />
      );
    }
  }
  return <>{a}</>;
}

export default AllPromotion;
