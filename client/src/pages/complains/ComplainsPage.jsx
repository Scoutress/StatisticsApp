import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./ComplainsPage.module.scss";

const ComplainsPage = () => {
  const [complains, setComplains] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const complainsResponse = await axios.get(
          "http://localhost:8080/complains/all"
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
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>There was an error fetching the data! {error.message}</div>;
  }

  return (
    <div className={styles.dcTicketsPage}>
      <h1 className={styles.title}>Complains List</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Employee ID</th>
            <th>Date</th>
            <th>Complains Count</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(complains) ? (
            complains.map((complain) => (
              <tr key={complain.id}>
                <td>{complain.id}</td>
                <td>{complain.employeeId}</td>
                <td>{complain.date}</td>
                <td>{complain.complainsCount}</td>
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

export default ComplainsPage;
