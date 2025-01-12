import { createBrowserRouter } from "react-router-dom";
import Root from "../root/Root";
import Routes from "../routes/Routes";
import ErrorPage from "../../pages/other/errorPage/ErrorPage";
import Homepage from "../../pages/home/HomePage";
import ProductivityPage from "../../pages/productivity/productivityPage/ProductivityPage";
import EmployeesPage from "../../pages/employees/employeesPage/EmployeesPage";
import EmployeeAddFormPage from "../../pages/employees/employeeAddFormPage/EmployeeAddFormPage";
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
      {
        path: Routes.employeesPage.link,
        element: <EmployeesPage />,
      },
      {
        path: Routes.employeeAddPage.link,
        element: <EmployeeAddFormPage />,
      },
    ],
  },
]);

export default Router;
