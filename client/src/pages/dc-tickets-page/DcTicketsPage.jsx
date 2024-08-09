import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./DcTicketsPage.module.scss";

const DcTicketsPage = () => {
  const [dcTicketsData, setDcTicketsData] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const employeesResponse = await axios.get(
          "http://localhost:8080/employee/all"
        );
        const dcTicketsResponse = await axios.get(
          "http://localhost:8080/dc-tickets/all"
        );

        setEmployees(employeesResponse.data);
        setDcTicketsData(dcTicketsResponse.data);
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

  const groupedData = dcTicketsData.reduce((acc, ticket) => {
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
    <div className={styles.dcTicketsPage}>
      <h1 className={styles.title}>Discord Tickets</h1>
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

export default DcTicketsPage;
