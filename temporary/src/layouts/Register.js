import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const url = "http://localhost:4000/rejestr";

function Register() {
  let navigate = useNavigate();
  const [data, setData] = useState({
    email: "email",
    username: "username",
    password: "",
  });

  const handleChange = (e) => {
    let dat = Object.assign({}, data);
    dat[e.target.name] = e.target.value;
    setData(dat);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(data.pas);
    if (data.username.length < 3) return;
    if (data.password.length < 8) return;
    if (data.email.indexOf("@") < 1) return;
    if (data.email.indexOf(" ") > -1) return;
    axios
      .post(url, {
        email: data.email,
        username: data.username,
        password: data.password,
      })
      .then((res) => {
        console.log(res.status);
      });
    setData({
      email: "",
      username: "",
      password: "",
    });
    navigate("../", { replace: true });
  };

  return (
    <div className="login-wrapper">
      <div className="login">
        <form onSubmit={handleSubmit}>
          <label>
            <input
              value={data.email}
              onChange={handleChange}
              name="email"
            ></input>
          </label>
          <label>
            <input
              value={data.username}
              onChange={handleChange}
              name="username"
            ></input>
          </label>
          <label>
            <input
              value={data.password}
              type="password"
              onChange={handleChange}
              name="password"
            ></input>
          </label>
          <button type="submit">Register</button>
        </form>
      </div>
    </div>
  );
}

export default Register;
