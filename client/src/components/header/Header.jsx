import { Link } from "react-router-dom";
import styles from "./Header.module.scss";

const Header = () => {
  return (
    <header className={styles.header}>
      <h1 className={styles.title}>Kaimux admin stats</h1>
      <nav className={styles.nav}>
        <Link to="/" className={styles.navLink}>
          Home
        </Link>
        <Link to="/stats/productivity" className={styles.navLink}>
          Productivity
        </Link>
        <Link to="/employee/all" className={styles.navLink}>
          Employees
        </Link>
        <Link to="/dc-tickets/add" className={styles.navLink}>
          Add dc tickets
        </Link>
        <Link to="/dc-tickets/all" className={styles.navLink}>
          Dc tickets
        </Link>
      </nav>
    </header>
  );
};

export default Header;
