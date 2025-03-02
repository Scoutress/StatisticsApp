import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "./ComplaintAddFormPage.module.scss";

const ComplainAddFormPage = () => {
  const [formData, setFormData] = useState({
    employeeId: "",
    date: "",
    text: "",
  });
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const today = new Date().toISOString().split("T")[0];
    setFormData((prevData) => ({
      ...prevData,
      joinDate: today,
    }));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    axios
      .post("http://localhost:8080/complaints/add", formData)
      .then((response) => {
        console.log("Complain added", response.data);
        navigate("/complaints/all-data");
      })
      .catch((error) => {
        setError(error);
        console.error("There was an error adding the complain!", error);
      });
  };

  return (
    <div className={styles.complainAddFormPage}>
      <h1 className={styles.title}>Add New Complain</h1>
      {error && <div className={styles.error}>Error: {error.message}</div>}
      <form onSubmit={handleSubmit} className={styles.form}>
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
          />
        </label>
        <label className={styles.label}>
          Complain:
          <textarea
            type="text"
            name="text"
            value={formData.text}
            onChange={handleChange}
            required
            className={styles.input}
          />
        </label>
        <button type="submit" className={styles.button}>
          Submit
        </button>
      </form>
    </div>
  );
};

export default ComplainAddFormPage;
