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
        <h2>MC Tickets</h2>
        <div className={styles.linkGroup}>
          <Link to="/mc-tickets/add">Add MC Tickets</Link>
          <Link to="/mc-tickets/all">MC Tickets</Link>
        </div>
      </div>

      <div className={styles.category}>
        <h2>DC Tickets</h2>
        <div className={styles.linkGroup}>
          <Link to="/dc-tickets/add">Add DC Tickets</Link>
          <Link to="/dc-tickets/all">DC Tickets</Link>
          <Link to="/dc-tickets/compare">DC Tickets Compare</Link>
        </div>
      </div>
    </div>
  );
};

export default Homepage;
