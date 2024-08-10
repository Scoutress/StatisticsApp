import { createBrowserRouter } from "react-router-dom";
import ErrorPage from "../../pages/error-page/ErrorPage.jsx";
import ProductivityPage from "../../pages/productivity-page/ProductivityPage.jsx";
import Home from "../../pages/home-page/HomePage.jsx";
import Routes from "../routes/Routes.jsx";
import Root from "../root/Root.jsx";
import EmployeesPage from "../../pages/employees-page/EmployeesPage.jsx";
import DcTicketsAddFormPage from "../../pages/dc-tickets-add-form-page/DcTicketsAddFormPage.jsx";
import DcTicketsPage from "../../pages/dc-tickets-page/DcTicketsPage.jsx";
import DcTicketsComparePage from "../../pages/dc-tickets-compare-page/DcTicketsComparePage.jsx";
import McTicketsAddFormPage from "../../pages/mc-tickets-add-form-page/McTicketsAddFormPage.jsx";
import McTicketsPage from "../../pages/mc-tickets-page/McTicketsPage.jsx";
import EmployeeAddFormPage from "../../pages/employee-add-form-page/EmployeeAddFormPage.jsx";
import PlaytimePage from "../../pages/playtime-page/PlaytimePage.jsx";

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
      {
        path: Routes.employeeAddPage.link,
        element: <EmployeeAddFormPage />,
      },
      {
        path: Routes.dcTicketsAddPage.link,
        element: <DcTicketsAddFormPage />,
      },
      {
        path: Routes.dcTicketsPage.link,
        element: <DcTicketsPage />,
      },
      {
        path: Routes.dcTicketsComparePage.link,
        element: <DcTicketsComparePage />,
      },
      {
        path: Routes.mcTicketsPage.link,
        element: <McTicketsPage />,
      },
      {
        path: Routes.mcTicketsAddPage.link,
        element: <McTicketsAddFormPage />,
      },
      {
        path: Routes.playtimePage.link,
        element: <PlaytimePage />,
      },
    ],
  },
]);

export default Router;
