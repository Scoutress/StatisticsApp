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
        <h2>Complains</h2>
        <div className={styles.linkGroup}>
          <Link to="/complains/all-sums">Complains Sum</Link>
          <Link to="/complains/all-data">Complains Data List</Link>
        </div>
      </div>
    </div>
  );
};

export default Homepage;
