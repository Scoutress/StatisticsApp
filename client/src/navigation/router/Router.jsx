import { createBrowserRouter } from "react-router-dom";
// import ErrorPage from "../../pages/error-page/ErrorPage.jsx";
// import ProductivityPage from "../../pages/productivity-page/ProductivityPage.jsx";
import Home from "../../pages/home/Home.jsx";
import Routes from "../routes/Routes.jsx";
import Root from "../root/Root.jsx";

const Router = createBrowserRouter([
  {
    element: <Root />,
    // errorElement: <ErrorPage />,
    children: [
      {
        path: Routes.homePage.link,
        element: <Home />,
      },
      // {
      //   path: Routes.productivityPage.link,
      //   element: <ProductivityPage />,
      // },
    ],
  },
]);

export default Router;
