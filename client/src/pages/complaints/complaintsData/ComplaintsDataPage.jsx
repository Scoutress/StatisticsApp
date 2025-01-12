import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import EditComplainModal from "../editComplaintModal/EditComplaintModal.jsx";
import { Link } from "react-router-dom";
import styles from "./ComplaintsDataPage.module.scss";

const ComplaintsDataPage = () => {
  const [complaints, setComplaints] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedComplain, setSelectedComplain] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const complaintsResponse = await axios.get(
          "http://localhost:8080/complaints/all-data"
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
    <div className={styles.complaintsPage}>
      <h1 className={styles.title}>Complaints</h1>

      <Link to="/complaints/add" key="complaints-add">
        Complain add
      </Link>

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
          {Array.isArray(complaints) ? (
            complaints.map((complain) => (
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

export default ComplaintsDataPage;
