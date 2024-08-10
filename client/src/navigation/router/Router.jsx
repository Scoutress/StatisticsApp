import { createBrowserRouter } from "react-router-dom";
import Root from "../root/Root";
import Routes from "../routes/Routes";
import ErrorPage from "../../pages/other/error-page/ErrorPage";
import Homepage from "../../pages/home/home-page/HomePage";
import ProductivityPage from "../../pages/productivity/productivity-page/ProductivityPage";
import EmployeesPage from "../../pages/employees/employees-page/EmployeesPage";
import EmployeeAddFormPage from "../../pages/employees/employee-add-form-page/EmployeeAddFormPage";
import DcTicketsAddFormPage from "../../pages/dc-tickets/dc-tickets-add-form-page/DcTicketsAddFormPage";
import DcTicketsPage from "../../pages/dc-tickets/dc-tickets-page/DcTicketsPage";
import DcTicketsComparePage from "../../pages/dc-tickets/dc-tickets-compare-page/DcTicketsComparePage";
import McTicketsPage from "../../pages/mc-tickets/mc-tickets-page/McTicketsPage";
import McTicketsAddFormPage from "../../pages/mc-tickets/mc-tickets-add-form-page/McTicketsAddFormPage";
import PlaytimePage from "../../pages/playtime/playtime-page/PlaytimePage";
import PlaytimeAddFormPage from "../../pages/playtime/playtime-add-form-page/PlaytimeAddFormPage.jsx";
import ComplainsPage from "../../pages/complains/ComplainsPage.jsx";

const Router = createBrowserRouter([
  {
    element: <Root />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: Routes.homePage.link,
        element: <Homepage />,
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
      {
        path: Routes.playtimeAddPage.link,
        element: <PlaytimeAddFormPage />,
      },
      {
        path: Routes.complainsPage.link,
        element: <ComplainsPage />,
      },
    ],
  },
]);

export default Router;
