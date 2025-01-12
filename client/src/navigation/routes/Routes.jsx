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
  complaintsSumPage: {
    link: "/complaints/all-sums",
    name: "Complaints sum",
  },
  complaintsDataPage: {
    link: "/complaints/all-data",
    name: "Complaints data",
  },
  complaintsAdd: {
    link: "/complaints/add",
    name: "Complaints add data",
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
