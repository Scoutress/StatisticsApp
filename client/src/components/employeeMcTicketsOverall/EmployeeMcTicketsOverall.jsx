import { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import styles from "./EmployeeMcTicketsOverall.module.scss";

const EmployeeMcTicketsOverall = () => {
  const [employees, setEmployees] = useState([]);
  const { employeeId } = useParams();

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/user/total-tickets"
        );
        setEmployees(response.data);
      } catch (error) {
        console.error("Error fetching employees with tickets:", error);
      }
    };
    fetchEmployees();
  }, []);

  return (
    <div className={styles.mcTicketsList}>
      <h2>Minecraft tickets for each employee</h2>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Number</th>
            <th>Username</th>
            <th>Total Tickets</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((employee, index) => (
            <tr
              key={employee.id}
              className={
                employee.id.toString() === employeeId ? styles.highlight : ""
              }
            >
              <td>{index + 1}</td>
              <td>{employee.username}</td>
              <td>{employee.totalTickets}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default EmployeeMcTicketsOverall;
