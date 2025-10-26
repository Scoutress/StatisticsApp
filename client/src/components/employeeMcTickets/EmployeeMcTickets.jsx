import { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import Loading from "../../components/loading/Loading.jsx";
import styles from "./EmployeeMcTickets.module.scss";

const EmployeeMcTickets = () => {
  const { employeeId } = useParams();
  const [mcTicketsData, setMcTicketsData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMcTicketsData = async () => {
      setError(null);
      try {
        const response = await axios.get(
          `http://localhost:8080/user/mcTickets/${employeeId}`
        );
        setMcTicketsData(response.data);
      } catch (error) {
        setError("Error fetching mc tickets data: " + error.message);
      } finally {
        setLoading(false);
      }
    };

    if (employeeId) {
      fetchMcTicketsData();
    }
  }, [employeeId]);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error} />}</div>;
  }

  return (
    <div className={styles.mcTicketsList}>
      <h2>Minecraft tickets</h2>
      {mcTicketsData ? (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Time Period</th>
              <th>Mc Tickets</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Last Day</td>
              <td>
                {mcTicketsData.lastDay !== undefined
                  ? mcTicketsData.lastDay
                  : "No data"}
              </td>
            </tr>
            <tr>
              <td>Last Week</td>
              <td>
                {mcTicketsData.lastWeek !== undefined
                  ? mcTicketsData.lastWeek
                  : "No data"}
              </td>
            </tr>
            <tr>
              <td>Last Month</td>
              <td>
                {mcTicketsData.lastMonth !== undefined
                  ? mcTicketsData.lastMonth
                  : "No data"}
              </td>
            </tr>
          </tbody>
        </table>
      ) : (
        <p>No mc tickets data available.</p>
      )}
    </div>
  );
};

export default EmployeeMcTickets;
