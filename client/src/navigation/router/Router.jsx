import { createBrowserRouter } from "react-router-dom";
import Root from "../root/Root";
import Routes from "../routes/Routes";
import ErrorPage from "../../pages/other/errorPage/ErrorPage";
import Homepage from "../../pages/home/HomePage";
import ProductivityPage from "../../pages/productivity/productivityPage/ProductivityPage";
import EmployeesPage from "../../pages/employees/employeesPage/EmployeesPage";
import EmployeeAddFormPage from "../../pages/employees/employeeAddFormPage/EmployeeAddFormPage";
import ComplaintsSumPage from "../../pages/complaints/complaintsSum/ComplaintsSumPage.jsx";
import ComplaintsDataPage from "../../pages/complaints/complaintsData/ComplaintsDataPage.jsx";
import ComplaintsAddFormPage from "../../pages/complaints/complaintAddFormPage/ComplaintAddFormPage.jsx";
import EmployeePersonalStatsPage from "../../pages/employeePersonalStats/employeePersonalStatsPage/EmployeePersonalStatsPage.jsx";
import SegmentStatsPage from "../../pages/playtime/SegmentStatsPage.jsx";
import LatestActivityPage from "../../pages/productivity/latestActivityPage/LatestActivityPage.jsx";

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
        path: Routes.latestActivityPage.link,
        element: <LatestActivityPage />,
      },
      {
        path: Routes.employeePersonalStats.link,
        element: <EmployeePersonalStatsPage />,
      },
      {
        path: Routes.complaintsSumPage.link,
        element: <ComplaintsSumPage />,
      },
      {
        path: Routes.complaintsDataPage.link,
        element: <ComplaintsDataPage />,
      },
      {
        path: Routes.complaintsAdd.link,
        element: <ComplaintsAddFormPage />,
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
        path: Routes.playtimeSegments.link,
        element: <SegmentStatsPage />,
      },
    ],
  },
]);

export default Router;
