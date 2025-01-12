import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./PlaytimePage.module.scss";
import { Link } from "react-router-dom";

const PlaytimePage = () => {
  const [playtimeData, setPlaytimeData] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const employeesResponse = await axios.get(
          "http://localhost:8080/employee/all"
        );
        const playtimeResponse = await axios.get(
          "http://localhost:8080/playtime/all"
        );

        setEmployees(employeesResponse.data);
        setPlaytimeData(playtimeResponse.data);
        setIsLoading(false);
      } catch (error) {
        setError(error);
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  if (isLoading) {
    return <div className={styles.loading}>Loading...</div>;
  }

  if (error) {
    return (
      <div className={styles.error}>
        There was an error fetching the data! {error.message}
      </div>
    );
  }

  const groupedData = playtimeData.reduce((acc, playtime) => {
    if (!acc[playtime.date]) {
      acc[playtime.date] = {};
    }
    acc[playtime.date][playtime.employeeId] = playtime;
    return acc;
  }, {});

  const sortedDates = Object.keys(groupedData).sort(
    (a, b) => new Date(b) - new Date(a)
  );

  return (
    <div className={styles.playtimePage}>
      <h1 className={styles.title}>Employee Playtime and AFK Playtime</h1>
      <Link to="/playtime/add">Playtime Add</Link>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Date</th>
            {employees.map((employee) => (
              <th
                key={employee.id}
                colSpan="2"
                className={styles.employeeHeader}
              >
                {employee.username}
              </th>
            ))}
          </tr>
          <tr>
            <th></th>
            {employees.map((employee) => (
              <React.Fragment key={employee.id}>
                <th>Playtime</th>
                <th>AFK Playtime</th>
              </React.Fragment>
            ))}
          </tr>
        </thead>
        <tbody>
          {sortedDates.map((date) => (
            <tr key={date}>
              <td>{date}</td>
              {employees.map((employee) => (
                <React.Fragment key={employee.id}>
                  <td>{groupedData[date][employee.id]?.hoursPlayed || 0}</td>
                  <td>{groupedData[date][employee.id]?.afkPlaytime || 0}</td>
                </React.Fragment>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default PlaytimePage;
