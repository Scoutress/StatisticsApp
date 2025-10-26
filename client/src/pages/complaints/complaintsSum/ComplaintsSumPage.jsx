import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import styles from "./ComplaintsSumPage.module.scss";

const ComplaintsSumPage = () => {
  const [complaints, setComplaints] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const complaintsResponse = await axios.get(
          "http://localhost:8080/complaints/all-sums"
        );

        const data = complaintsResponse.data;

        console.log("API response:", data);

        if (Array.isArray(data)) {
          setComplaints(data);
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
    <div className={styles.complaintsPage}>
      <h1 className={styles.title}>Complaints Sum</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Employee ID</th>
            <th>Complaints Count</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(complaints) ? (
            complaints.map((complaint) => (
              <tr key={complaint.id}>
                <td>{complaint.employeeId}</td>
                <td>{complaint.value}</td>
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

export default ComplaintsSumPage;
