import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./DcTicketsComparePage.module.scss";

const DcTicketsComparePage = () => {
  const [dcComparedTicketsData, setDcComparedTicketsData] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const employeesResponse = await axios.get(
          "http://localhost:8080/employee/all"
        );
        const dcComparedTicketsResponse = await axios.get(
          "http://localhost:8080/dc-tickets/compare"
        );

        setEmployees(employeesResponse.data);
        setDcComparedTicketsData(dcComparedTicketsResponse.data);
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

  const groupedData = dcComparedTicketsData.reduce((acc, item) => {
    if (!acc[item.date]) {
      acc[item.date] = {};
    }
    acc[item.date][item.employeeId] = item.percentage.toFixed(2);
    return acc;
  }, {});

  return (
    <div className={styles.employeeTicketPercentagePage}>
      <h1 className={styles.title}>Employee Ticket Percentages</h1>
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
          {Object.entries(groupedData).map(([date, percentages]) => (
            <tr key={date}>
              <td>{date}</td>
              {employees.map((employee) => (
                <td key={employee.id}>
                  {percentages[employee.id] !== undefined
                    ? `${percentages[employee.id]}%`
                    : "N/A"}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DcTicketsComparePage;
