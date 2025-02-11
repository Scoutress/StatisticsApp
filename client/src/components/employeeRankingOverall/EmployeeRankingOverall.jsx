import { useState, useEffect } from "react";
import axios from "axios";
import PropTypes from "prop-types";
import styles from "./EmployeeRankingOverall.module.scss";

const EmployeeRankingOverall = ({ employeeId }) => {
  const [ranking, setRanking] = useState(null);
  const [totalEmployees, setTotalEmployees] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRanking = async () => {
      setError(null);
      try {
        const response = await axios.get(
          `http://localhost:8080/user/ranking/${employeeId}`
        );
        setRanking(response.data.rank);
        setTotalEmployees(response.data.totalEmployees);
      } catch (error) {
        setError(
          "There was an error fetching the ranking data! " + error.message
        );
      } finally {
        setLoading(false);
      }
    };

    if (employeeId) {
      fetchRanking();
    }
  }, [employeeId]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className={styles.rankingContainer}>
      <h2>Ranking</h2>
      <p>
        <strong>{ranking}</strong> / {totalEmployees}
      </p>
    </div>
  );
};

EmployeeRankingOverall.propTypes = {
  employeeId: PropTypes.number.isRequired,
};

export default EmployeeRankingOverall;
