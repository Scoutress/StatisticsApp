import styles from "./ErrorMessage.module.scss";
import PropTypes from "prop-types";

const ErrorMessage = ({ message }) => {
  return (
    <div className={styles.errorMessage}>
      <p>{message}</p>
    </div>
  );
};

ErrorMessage.propTypes = {
  message: PropTypes.string.isRequired,
};

export default ErrorMessage;
