import { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import styles from "./HomePage.module.scss";

const Homepage = () => {
  const [employees, setEmployees] = useState([]);

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const response = await axios.get("http://localhost:8080/employee/all");
        setEmployees(response.data);
      } catch (error) {
        console.error("Error fetching employees:", error);
      }
    };
    fetchEmployees();
  }, []);

  return (
    <div className={styles.homePage}>
      <div className={styles.category}>
        <h2>Productivity</h2>
        <div className={styles.linkGroup}>
          <Link to="/stats/productivity">Productivity</Link>
        </div>
      </div>

      <div className={styles.category}>
        <h2>Main stats (what employee can see)</h2>
        <div className={styles.linkGroup}>
          {employees.map((employee) => (
            <Link
              key={employee.id}
              to={`/user/stats/${employee.id}`}
              className={styles.employeeButton}
            >
              {employee.username} main stats
            </Link>
          ))}
        </div>
      </div>

      <div className={styles.category}>
        <h2>Employees</h2>
        <div className={styles.linkGroup}>
          <Link to="/employee/all">Employees</Link>
        </div>
      </div>

      <div className={styles.category}>
        <h2>Complaints</h2>
        <div className={styles.linkGroup}>
          <Link to="/complaints/all-sums">Complaints Sum</Link>
          <Link to="/complaints/all-data">Complaints Data List</Link>
        </div>
      </div>
    </div>
  );
};

export default Homepage;
