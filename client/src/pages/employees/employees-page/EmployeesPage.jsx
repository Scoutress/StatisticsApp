import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import styles from "./EmployeesPage.module.scss";
import { Link } from "react-router-dom";

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
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error.message} />}</div>;
  }

  return (
    <div className={styles.employeesPage}>
      <h1 className={styles.title}>Employees list</h1>

      <Link to="/employee/add" key="employee-add">
        Employee add
      </Link>

      {isLoading && (
        <div className="spinner">
          <p>Updating...</p>
        </div>
      )}

      <table className={styles.table}>
        <thead>
          <tr>
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
