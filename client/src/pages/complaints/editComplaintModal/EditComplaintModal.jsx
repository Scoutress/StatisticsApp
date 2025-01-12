import { useState } from "react";
import axios from "axios";
import PropTypes from "prop-types";
import styles from "./EditComplaintModal.module.scss";

const EditComplaintModal = ({ complaint, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({ ...complaint });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSave = (e) => {
    e.preventDefault();
    axios
      .put(`http://localhost:8080/complaints/${complaint.id}`, formData)
      .then((response) => {
        console.log("Complaint updated", response.data);
        onUpdate();
        onClose();
      })
      .catch((error) => {
        console.error("There was an error updating the complaint!", error);
      });
  };

  return (
    <div className={styles.modal}>
      <div className={styles.modalContent}>
        <h2>Edit Complaint</h2>
        <form onSubmit={handleSave} className={styles.form}>
          <label className={styles.label}>
            Employee ID:
            <input
              type="text"
              name="employeeId"
              value={formData.employeeId}
              onChange={handleChange}
              required
              className={styles.input}
            />
          </label>
          <label className={styles.label}>
            Date:
            <input
              type="text"
              name="date"
              value={formData.date}
              onChange={handleChange}
              required
              className={styles.input}
              pattern="\d{4}-\d{2}-\d{2}"
              placeholder="yyyy-mm-dd"
            />
          </label>
          <label className={styles.label}>
            Complaint:
            <textarea
              name="text"
              value={formData.text}
              onChange={handleChange}
              required
              className={styles.textarea}
            />
          </label>
          <div className={styles.buttonContainer}>
            <button type="submit" className={styles.saveButton}>
              Save
            </button>
            <button
              type="button"
              onClick={onClose}
              className={styles.cancelButton}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

EditComplaintModal.propTypes = {
  complaint: PropTypes.shape({
    id: PropTypes.number.isRequired,
    employeeId: PropTypes.number.isRequired,
    date: PropTypes.string.isRequired,
    complaint: PropTypes.string.isRequired,
  }).isRequired,
  onClose: PropTypes.func.isRequired,
  onUpdate: PropTypes.func.isRequired,
};

export default EditComplaintModal;
