const Routes = {
  homePage: {
    link: "/",
    name: "Home",
  },
  productivityPage: {
    link: "/stats/productivity",
    name: "Productivity",
  },
  employeePersonalStats: {
    link: "/user/stats/:employeeId",
    name: "Productivity",
  },
  complainsSumPage: {
    link: "/complains/all-sums",
    name: "Complaints sum",
  },
  complainsDataPage: {
    link: "/complains/all-data",
    name: "Complaints data",
  },
  employeesPage: {
    link: "/employee/all",
    name: "Employees",
  },
  employeeAddPage: {
    link: "/employee/add",
    name: "Employee Add",
  },
};

export default Routes;
