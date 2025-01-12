import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import styles from "./EmployeesPage.module.scss";
import { Link } from "react-router-dom";
import EditEmployeeModal from "../editEmployeeModal/EditEmployeeModal.jsx";

const EmployeesPage = () => {
  const [employeesData, setEmployeesData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedEmployee, setSelectedEmployee] = useState(null);

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

  useEffect(() => {
    fetchData();
  }, []);

  const handleEditClick = (employee) => {
    setSelectedEmployee(employee);
  };

  const closeModal = () => {
    setSelectedEmployee(null);
  };

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
            <th>Actions</th>
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
              <td>
                <button onClick={() => handleEditClick(employee)}>Edit</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedEmployee && (
        <EditEmployeeModal
          employee={selectedEmployee}
          onClose={closeModal}
          onUpdate={fetchData}
        />
      )}
    </div>
  );
};

export default EmployeesPage;
