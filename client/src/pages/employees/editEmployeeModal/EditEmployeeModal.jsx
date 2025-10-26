import { useState } from "react";
import axios from "axios";
import PropTypes from "prop-types";
import styles from "./EditEmployeeModal.module.scss";
import ConfirmationModal from "./../../../components/confirmationModal/ConfirmationModal.jsx";

const EditEmployeeModal = ({ employee, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({ ...employee });
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [confirmationMessage, setConfirmationMessage] = useState("");
  const [confirmationAction, setConfirmationAction] = useState(null);
  const levels = [
    "Owner",
    "Operator",
    "Manager",
    "Organizer",
    "Overseer",
    "ChatMod",
    "Support",
    "Helper",
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSave = (e) => {
    e.preventDefault();
    axios
      .put(`http://localhost:8080/employee/${employee.id}`, formData)
      .then((response) => {
        console.log("Employee updated", response.data);
        onUpdate();
        onClose();
      })
      .catch((error) => {
        console.error("There was an error updating the employee!", error);
      });
  };

  const handleDelete = () => {
    setConfirmationMessage(
      `Are you sure you want to delete ${formData.username}?`
    );
    setConfirmationAction(() => () => {
      axios
        .delete(`http://localhost:8080/employee/${employee.id}`)
        .then((response) => {
          console.log("Employee deleted", response.data);
          onUpdate();
          onClose();
        })
        .catch((error) => {
          console.error("There was an error deleting the employee!", error);
        });
    });
    setShowConfirmation(true);
  };

  const handlePromote = () => {
    const currentLevelIndex = levels.indexOf(formData.level);
    if (currentLevelIndex > 0) {
      setConfirmationMessage(
        `Are you sure you want to promote ${formData.username} to ${
          levels[currentLevelIndex - 1]
        }?`
      );
      setConfirmationAction(() => () => {
        setFormData({ ...formData, level: levels[currentLevelIndex - 1] });
        setShowConfirmation(false);
      });
      setShowConfirmation(true);
    }
  };

  const handleDemote = () => {
    const currentLevelIndex = levels.indexOf(formData.level);
    if (currentLevelIndex < levels.length - 1) {
      setConfirmationMessage(
        `Are you sure you want to demote ${formData.username} to ${
          levels[currentLevelIndex + 1]
        }?`
      );
      setConfirmationAction(() => () => {
        setFormData({ ...formData, level: levels[currentLevelIndex + 1] });
        setShowConfirmation(false);
      });
      setShowConfirmation(true);
    }
  };

  const closeConfirmationModal = () => {
    setShowConfirmation(false);
  };

  return (
    <div className={styles.modal}>
      <div className={styles.modalContent}>
        <h2>{formData.username}</h2>
        <form onSubmit={handleSave} className={styles.form}>
          <label className={styles.label}>
            Username:
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              className={styles.input}
            />
          </label>
          <label className={styles.label}>
            Level:
            <div className={styles.levelWrapper}>
              <input
                type="text"
                name="level"
                value={formData.level}
                onChange={handleChange}
                required
                className={styles.input}
                readOnly
              />
              <button
                type="button"
                onClick={handlePromote}
                className={styles.promoteButton}
              >
                Promote
              </button>
              <button
                type="button"
                onClick={handleDemote}
                className={styles.demoteButton}
              >
                Demote
              </button>
            </div>
          </label>
          <label className={styles.label}>
            First Name:
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              className={styles.input}
            />
          </label>
          <label className={styles.label}>
            Last Name:
            <input
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              className={styles.input}
            />
          </label>
          <label className={styles.label}>
            Email:
            <input
              type="text"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className={styles.input}
            />
          </label>
          <label className={styles.label}>
            Language:
            <input
              type="text"
              name="language"
              value={formData.language}
              onChange={handleChange}
              className={styles.input}
            />
          </label>
          <label className={styles.label}>
            Join Date:
            <input
              type="text"
              name="joinDate"
              value={formData.joinDate}
              onChange={handleChange}
              required
              pattern="\d{4}-\d{2}-\d{2}"
              placeholder="yyyy-mm-dd"
              className={styles.input}
            />
          </label>
          <div className={styles.buttonContainer}>
            <button
              type="button"
              onClick={handleDelete}
              className={styles.deleteButton}
            >
              Delete Employee
            </button>
            <button type="submit" className={styles.saveButton}>
              Save
            </button>
          </div>
        </form>
        <button onClick={onClose} className={styles.closeButton}>
          Close
        </button>
      </div>

      {showConfirmation && (
        <ConfirmationModal
          message={confirmationMessage}
          onConfirm={confirmationAction}
          onCancel={closeConfirmationModal}
        />
      )}
    </div>
  );
};

EditEmployeeModal.propTypes = {
  employee: PropTypes.shape({
    id: PropTypes.number.isRequired,
    username: PropTypes.string.isRequired,
    level: PropTypes.string.isRequired,
    firstName: PropTypes.string.isRequired,
    lastName: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired,
    language: PropTypes.string.isRequired,
    joinDate: PropTypes.string.isRequired,
  }).isRequired,
  onClose: PropTypes.func.isRequired,
  onUpdate: PropTypes.func.isRequired,
};

export default EditEmployeeModal;
