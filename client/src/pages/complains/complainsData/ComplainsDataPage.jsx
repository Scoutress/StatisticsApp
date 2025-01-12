import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import EditComplainModal from "./../../../components/editComplainModal/EditComplainModal.jsx";
import styles from "./ComplainsDataPage.module.scss";

const ComplainsDataPage = () => {
  const [complains, setComplains] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedComplain, setSelectedComplain] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const complainsResponse = await axios.get(
          "http://localhost:8080/complains/all-data"
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

  const handleEditClick = (complain) => {
    setSelectedComplain(complain);
  };

  const closeModal = () => {
    setSelectedComplain(null);
  };

  if (isLoading) {
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error.message} />}</div>;
  }

  return (
    <div className={styles.complainsPage}>
      <h1 className={styles.title}>Complains</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Employee ID</th>
            <th>Date</th>
            <th>Text</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(complains) ? (
            complains.map((complain) => (
              <tr key={complain.id}>
                <td>{complain.employeeId}</td>
                <td>{complain.date}</td>
                <td>{complain.text}</td>
                <td>
                  <button onClick={() => handleEditClick(complain)}>
                    Edit
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4">No data available</td>
            </tr>
          )}
        </tbody>
      </table>

      {selectedComplain && (
        <EditComplainModal
          complain={selectedComplain}
          onClose={closeModal}
          onUpdate={() => window.location.reload()}
        />
      )}
    </div>
  );
};

export default ComplainsDataPage;
