import React from "react";
import { Outlet } from "react-router-dom";
import Header from "../../components/header/Header.tsx";

const Root = () => {
  return (
    <>
      <Header />
      <Outlet />
    </>
  );
};

export default Root;
