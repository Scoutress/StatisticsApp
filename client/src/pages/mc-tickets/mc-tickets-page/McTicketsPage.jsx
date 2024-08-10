import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./McTicketsPage.module.scss";
import { Link } from "react-router-dom";

const McTicketsPage = () => {
  const [mcTicketsData, setMcTicketsData] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const employeesResponse = await axios.get(
          "http://localhost:8080/employee/all"
        );
        const mcTicketsResponse = await axios.get(
          "http://localhost:8080/mc-tickets/all"
        );

        const sortedEmployees = employeesResponse.data.sort(
          (a, b) => a.id - b.id
        );

        setEmployees(sortedEmployees);
        setMcTicketsData(mcTicketsResponse.data);
        setIsLoading(false);
      } catch (error) {
        setError(error);
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  if (isLoading) {
    return <div className={styles.loading}>Loading...</div>;
  }

  if (error) {
    return (
      <div className={styles.error}>
        There was an error fetching the data! {error.message}
      </div>
    );
  }

  const groupedData = mcTicketsData.reduce((acc, ticket) => {
    if (!acc[ticket.date]) {
      acc[ticket.date] = {};
    }
    acc[ticket.date][ticket.employeeId] = ticket.ticketCount;
    return acc;
  }, {});

  const sortedDates = Object.keys(groupedData).sort(
    (a, b) => new Date(b) - new Date(a)
  );

  return (
    <div className={styles.mcTicketsPage}>
      <h1 className={styles.title}>Minecraft Tickets</h1>
      <Link to="/mc-tickets/add">Add MC Tickets</Link>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Date</th>
            {employees.map((employee) => (
              <th key={employee.id}>{employee.username}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {sortedDates.map((date) => (
            <tr key={date}>
              <td>{date}</td>
              {employees.map((employee) => (
                <td key={employee.id}>{groupedData[date][employee.id] || 0}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default McTicketsPage;
