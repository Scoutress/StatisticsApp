import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import HalfDoughnutChart from "../../../components/halfDoughnutChart/HalfDoughnutChart.jsx";
import EmployeeRankingOverall from "../../../components/employeeRankingOverall/EmployeeRankingOverall.jsx";
import UserRecommendationText from "../../../components/userRecommendation/UserRecommendationText.jsx";
import styles from "./EmployeePersonalStatsPage.module.scss";
import EmployeeMcTickets from "../employeeMcTickets/EmployeeMcTickets.jsx";

const EmployeePersonalStatsPage = () => {
  const { employeeId } = useParams();
  const [productivity, setProductivity] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProductivity = async () => {
      setError(null);
      try {
        const response = await axios.get(
          `http://localhost:8080/user/stats/${employeeId}`
        );
        setProductivity(response.data);
      } catch (error) {
        setError("There was an error fetching the data! " + error.message);
      } finally {
        setLoading(false);
      }
    };

    if (employeeId) {
      fetchProductivity();
    }
  }, [employeeId]);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error} />}</div>;
  }

  return (
    <div className={styles.employeeStatsPage}>
      {productivity !== null ? (
        <HalfDoughnutChart productivity={productivity} />
      ) : (
        <p>No productivity data available.</p>
      )}
      {employeeId !== null ? (
        <EmployeeRankingOverall employeeId={employeeId} />
      ) : (
        <p>No ranking data available.</p>
      )}
      {employeeId !== null ? (
        <UserRecommendationText employeeId={employeeId} />
      ) : (
        <p>No recommendation data available.</p>
      )}
      {employeeId !== null ? (
        <EmployeeMcTickets employeeId={employeeId} />
      ) : (
        <p>No recommendation data available.</p>
      )}
    </div>
  );
};

export default EmployeePersonalStatsPage;
