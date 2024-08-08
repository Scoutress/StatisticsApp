import { createBrowserRouter } from "react-router-dom";
import ErrorPage from "../../pages/error-page/ErrorPage.jsx";
import ProductivityPage from "../../pages/productivity-page/ProductivityPage.jsx";
import Home from "../../pages/home-page/HomePage.jsx";
import Routes from "../routes/Routes.jsx";
import Root from "../root/Root.jsx";
import EmployeesPage from "../../pages/employees-page/EmployeesPage.jsx";

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
      {
        path: Routes.employeesPage.link,
        element: <EmployeesPage />,
      },
    ],
  },
]);

export default Router;
