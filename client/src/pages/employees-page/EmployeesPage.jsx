import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./EmployeesPage.module.scss";

const EmployeesPage = () => {
  const [employeesData, setEmployeesData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      axios
        .get("http://localhost:8080/employee/all")
        .then((response) => {
          setEmployeesData(response.data);
          setIsLoading(false);
        })
        .catch((error) => {
          setError(error);
          setIsLoading(false);
        });
    };
    fetchData();
  }, []);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>There was an error fetching the data! {error.message}</div>;
  }

  return (
    <div className={styles.employeesPage}>
      <h1 className={styles.title}>Employees list</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Level</th>
            <th>First name</th>
            <th>Last name</th>
            <th>Email</th>
            <th>Language</th>
            <th>Join Date</th>
          </tr>
        </thead>
        <tbody>
          {employeesData.map((employee) => (
            <tr key={employee.id}>
              <td>{employee.id}</td>
              <td>{employee.username}</td>
              <td>{employee.level}</td>
              <td>{employee.firstName}</td>
              <td>{employee.lastName}</td>
              <td>{employee.email}</td>
              <td>{employee.language}</td>
              <td>{employee.joinDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default EmployeesPage;
