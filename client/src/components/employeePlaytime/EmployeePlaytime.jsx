import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import Loading from "../../components/loading/Loading.jsx";
import ErrorMessage from "../../components/errorMessage/ErrorMessage.jsx";
import styles from "./EmployeePlaytime.module.scss";

const EmployeePlaytime = () => {
  const { employeeId } = useParams();
  const [playtimeData, setPlaytimeData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPlaytimeData = async () => {
      setError(null);
      try {
        const response = await axios.get(
          `http://localhost:8080/user/playtime/${employeeId}`
        );
        setPlaytimeData(response.data);
      } catch (error) {
        setError("Error fetching playtime data: " + error.message);
      } finally {
        setLoading(false);
      }
    };

    if (employeeId) {
      fetchPlaytimeData();
    }
  }, [employeeId]);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error} />}</div>;
  }

  return (
    <div className={styles.playtimeList}>
      <h2>Playtimes</h2>
      {playtimeData ? (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Time Period</th>
              <th>Playtime (hours)</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Last Day</td>
              <td>
                {playtimeData.lastDay !== undefined
                  ? playtimeData.lastDay
                  : "No data"}
              </td>
            </tr>
            <tr>
              <td>Last Week</td>
              <td>
                {playtimeData.lastWeek !== undefined
                  ? playtimeData.lastWeek
                  : "No data"}
              </td>
            </tr>
            <tr>
              <td>Last Month</td>
              <td>
                {playtimeData.lastMonth !== undefined
                  ? playtimeData.lastMonth
                  : "No data"}
              </td>
            </tr>
          </tbody>
        </table>
      ) : (
        <p>No playtime data available.</p>
      )}
    </div>
  );
};

export default EmployeePlaytime;
