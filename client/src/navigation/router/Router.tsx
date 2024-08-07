import { createBrowserRouter } from "react-router-dom";
import ErrorPage from "../../pages/error-page/ErrorPage.tsx";
import ProductivityPage from "../../pages/productivity-page/ProductivityPage.tsx";
import Home from "../../pages/home/Home.tsx";
import Routes from "../routes/Routes.tsx";
import Root from "../root/Root.tsx";
import React from "react";

const Router = createBrowserRouter([
  {
    element: <Root />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: Routes.homePage.link,
        element: <Home />,
      },
      {
        path: Routes.productivityPage.link,
        element: <ProductivityPage />,
      },
    ],
  },
]);

export default Router;
