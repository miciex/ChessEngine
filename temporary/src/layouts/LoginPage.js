import React, { useState } from "react";
import "../css/LoginPage.css";
import axios from "axios";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

const url = "http://localhost:4000/login";

function LoginPage(props) {
  const [state, setState] = useState({
    username: "person",
    password: "Example",
  });

  const handleChange = (e) => {
    let newState = { ...state };
    newState[e.target.name] = e.target.value;
    setState(newState);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    axios
      .post(url, {
        username: state.username,
        password: state.password,
      })
      .then((res) => {
        console.log(res.status);
        if (res.status === 200) {
          props.login();
        }
      });
  };
  return (
    <>
      <div className="login-wrapper">
        <div className="login">
          <form onSubmit={handleSubmit}>
            <label>
              <input
                value={state.username}
                onChange={handleChange}
                name="username"
              ></input>
            </label>
            <label>
              <input
                value={state.password}
                onChange={handleChange}
                name="password"
                type="password"
              ></input>
            </label>
            <button type="submit">Login</button>
            <Link to={"/register"}>register</Link>
          </form>
        </div>
      </div>
    </>
  );
}

export default LoginPage;
