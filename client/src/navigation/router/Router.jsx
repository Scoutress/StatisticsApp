import { createBrowserRouter } from "react-router-dom";
import Root from "../root/Root";
import Routes from "../routes/Routes";
import ErrorPage from "../../pages/other/errorPage/ErrorPage";
import Homepage from "../../pages/home/HomePage";
import ProductivityPage from "../../pages/productivity/productivityPage/ProductivityPage";
import EmployeesPage from "../../pages/employees/employeesPage/EmployeesPage";
import EmployeeAddFormPage from "../../pages/employees/employeeAddFormPage/EmployeeAddFormPage";
import DcTicketsAddFormPage from "../../pages/dcTickets/dcTicketsAddFormPage/DcTicketsAddFormPage";
import DcTicketsPage from "../../pages/dcTickets/dcTicketsPage/DcTicketsPage";
import DcTicketsComparePage from "../../pages/dcTickets/dcTicketsComparePage/DcTicketsComparePage";
import McTicketsPage from "../../pages/mcTickets/mcTicketsPage/McTicketsPage";
import McTicketsAddFormPage from "../../pages/mcTickets/mcTicketsAddFormPage/McTicketsAddFormPage";
import PlaytimePage from "../../pages/playtime/playtimePage/PlaytimePage";
import PlaytimeAddFormPage from "../../pages/playtime/playtimeAddFormPage/PlaytimeAddFormPage.jsx";
import ComplainsSumPage from "../../pages/complains/complainsSum/ComplainsSumPage.jsx";
import ComplainsDataPage from "../../pages/complains/complainsData/ComplainsDataPage.jsx";
import EmployeePersonalStatsPage from "../../pages/employeePersonalStats/EmployeePersonalStatsPage.jsx";

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
        path: Routes.employeePersonalStats.link,
        element: <EmployeePersonalStatsPage />,
      },
      {
        path: Routes.complainsSumPage.link,
        element: <ComplainsSumPage />,
      },
      {
        path: Routes.complainsDataPage.link,
        element: <ComplainsDataPage />,
      },
      ///////////////////
      // Old links
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
    ],
  },
]);

export default Router;
