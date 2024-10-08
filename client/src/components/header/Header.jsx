import { Link } from "react-router-dom";
import styles from "./Header.module.scss";

const Header = () => {
  return (
    <header className={styles.header}>
      <h1 className={styles.title}>Kaimux administration statistics</h1>
      <nav className={styles.nav}>
        <Link to="/" className={styles.navLink}>
          Home
        </Link>
        <Link to="/stats/productivity" className={styles.navLink}>
          Productivity
        </Link>
      </nav>
    </header>
  );
};

export default Header;
