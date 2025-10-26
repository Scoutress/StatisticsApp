import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../components/loading/Loading.jsx";
import ErrorMessage from "../../components/errorMessage/ErrorMessage.jsx";
import SegmentVisualization from "../../components/playtimeSegments/SegmentVisualization.jsx";
import styles from "./SegmentStatsPage.module.scss";

const SegmentStatsPage = () => {
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [segmentData, setSegmentData] = useState([]);
  const [serverSegmentData, setServerSegmentData] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const response = await axios.get("http://localhost:8080/employee/all");
        setEmployees(response.data);

        if (response.data.length > 0) {
          const firstEmployee = response.data[0];
          setSelectedEmployee(firstEmployee.id);
          fetchSegmentData(firstEmployee.id);
          const servers = [
            "Survival",
            "Skyblock",
            "Creative",
            "Boxpvp",
            "Prison",
            "Events",
            "Lobby",
          ];
          servers.forEach((server) =>
            fetchServerSegmentData(firstEmployee.id, server)
          );
        }
      } catch (err) {
        console.error("Error fetching employees:", err);
        setError("Unable to load employees. Please try again.");
      }
    };
    fetchEmployees();
  }, []);

  const fetchSegmentData = async (employeeId) => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(
        `http://localhost:8080/statistics/segments/${employeeId}`
      );
      setSegmentData(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error("Error fetching data for all servers:", err);
      setError("Unable to load data for all servers.");
    } finally {
      setLoading(false);
    }
  };

  const fetchServerSegmentData = async (employeeId, serverName) => {
    try {
      const encodedServerName = encodeURIComponent(serverName);
      const response = await axios.get(
        `http://localhost:8080/statistics/segments/${employeeId}/${encodedServerName}`
      );
      setServerSegmentData((prevData) => ({
        ...prevData,
        [serverName]: Array.isArray(response.data) ? response.data : [],
      }));
    } catch (err) {
      console.error(`Error fetching data for server "${serverName}":`, err);
      setError(`Unable to load data for server "${serverName}".`);
    }
  };

  const handleEmployeeClick = (employeeId) => {
    setSelectedEmployee(employeeId);
    setSegmentData([]);
    setServerSegmentData({});

    fetchSegmentData(employeeId);

    const servers = [
      "Survival",
      "Skyblock",
      "Creative",
      "Boxpvp",
      "Prison",
      "Events",
      "Lobby",
    ];
    servers.forEach((server) => fetchServerSegmentData(employeeId, server));
  };

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  return (
    <div className={styles.pageContainer}>
      <div className={styles.employeeList}>
        <h3>Employees</h3>
        <ul className={styles.employeeListItems}>
          {employees.map((employee) => (
            <li
              key={employee.id}
              onClick={() => handleEmployeeClick(employee.id)}
              className={`${styles.employeeItem} ${
                selectedEmployee === employee.id ? styles.selectedItem : ""
              }`}
            >
              {employee.username}
            </li>
          ))}
        </ul>
      </div>

      <div className={styles.contentSection}>
        <h2>Playtime Statistics</h2>

        <div className={styles.visualizationContainer}>
          <h3>All Servers</h3>
          {selectedEmployee ? (
            <SegmentVisualization segmentData={segmentData} />
          ) : (
            <p>Please select an employee to see statistics.</p>
          )}
        </div>

        {[
          "Survival",
          "Skyblock",
          "Creative",
          "Boxpvp",
          "Prison",
          "Events",
          "Lobby",
        ].map((serverName, index) => {
          const serverData = serverSegmentData[serverName];
          if (!serverData || serverData.length === 0) {
            return null;
          }
          return (
            <div
              key={`${serverName}-${index}`}
              className={styles.visualizationContainer}
            >
              <h3>{serverName}</h3>
              <SegmentVisualization segmentData={serverData} />
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default SegmentStatsPage;
