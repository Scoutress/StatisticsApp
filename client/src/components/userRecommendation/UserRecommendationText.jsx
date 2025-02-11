import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import styles from "./UserRecommendationText.module.scss";
import Loading from "../../components/loading/Loading";
import ErrorMessage from "../../components/errorMessage/ErrorMessage";

const EmployeeRecommendation = () => {
  const { employeeId } = useParams();
  const [recommendation, setRecommendation] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRecommendation = async () => {
      setError(null);
      try {
        const response = await axios.get(
          `http://localhost:8080/user/recommendation/${employeeId}`
        );
        setRecommendation(response.data.text);
      } catch (error) {
        setError(
          "There was an error fetching the recommendation! " + error.message
        );
      } finally {
        setLoading(false);
      }
    };

    if (employeeId) {
      fetchRecommendation();
    }
  }, [employeeId]);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error} />}</div>;
  }

  return (
    <div className={styles.recommendationContainer}>
      <h2>Recommendation</h2>
      <p>{recommendation}</p>
    </div>
  );
};

export default EmployeeRecommendation;
