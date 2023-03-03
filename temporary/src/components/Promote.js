import boardhandle from "./boardhandle";

function Promote({ x, y, value, click, state }) {
  let data;
  return (
    <div
      className="promote"
      style={{ top: `${y * 12.5}%`, left: `${x * 12.5}%` }}
      onClick={() => {
        data = boardhandle(state, x, y > 3 ? 7 : 0, value);
        if (data !== null)
          click(data);
      }}
    >
      {value}
    </div>
  );
}

export default Promote;
