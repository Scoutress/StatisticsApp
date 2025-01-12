import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import styles from "./ComplainsSumPage.module.scss";

const ComplainsSumPage = () => {
  const [complains, setComplains] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const complainsResponse = await axios.get(
          "http://localhost:8080/complains/all-sums"
        );

        const data = complainsResponse.data;

        console.log("API response:", data);

        if (Array.isArray(data)) {
          setComplains(data);
        } else {
          setError(new Error("Expected an array but got something else"));
        }

        setIsLoading(false);
      } catch (error) {
        setError(error);
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  if (isLoading) {
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error} />}</div>;
  }

  return (
    <div className={styles.complainsPage}>
      <h1 className={styles.title}>Complains Sum</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Employee ID</th>
            <th>Complains Count</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(complains) ? (
            complains.map((complain) => (
              <tr key={complain.id}>
                <td>{complain.employeeId}</td>
                <td>{complain.value}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4">No data available</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ComplainsSumPage;
