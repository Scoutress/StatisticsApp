import { Link } from "react-router-dom";
import styles from "./HomePage.module.scss";

const Homepage = () => {
  return (
    <div className={styles.homePage}>
      <div className={styles.category}>
        <h2>Productivity</h2>
        <div className={styles.linkGroup}>
          <Link to="/stats/productivity">Productivity</Link>
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
