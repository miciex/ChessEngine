import React, { useState } from "react";
import Lobby from "./Lobby";
import LoginPage from "./LoginPage";
import { useNavigate } from "react-router-dom";

function Login(props) {
  let navigate = useNavigate();

  const handleLogin = () => {
    navigate("../lobby", { replace: true });
  };
  return (
    <>
      <LoginPage login={handleLogin} />
    </>
  );
}

export default Login;
