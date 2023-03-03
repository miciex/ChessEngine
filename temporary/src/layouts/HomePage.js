import React from "react";
import { Link } from "react-router-dom";

function HomePage() {
  return (
    <>
      <div>
        <p>
          <Link to="/login">Login</Link>
        </p>
        <p>
          <Link to="/register">Register</Link>
        </p>
        <p>
          <Link to="/game">Join game as a guest</Link>
        </p>
        <p>
          <Link to="lobby">Play as a guest</Link>
        </p>
      </div>
    </>
  );
}

export default HomePage;
