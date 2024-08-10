import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "./EmployeeAddFormPage.module.scss";

const EmployeeAddFormPage = () => {
  const [formData, setFormData] = useState({
    username: "",
    level: "",
    firstName: "",
    lastName: "",
    email: "",
    language: "",
    joinDate: "",
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
      .post("http://localhost:8080/employee/add", formData)
      .then((response) => {
        console.log("Employee added", response.data);
        navigate("/employee/all");
      })
      .catch((error) => {
        setError(error);
        console.error("There was an error adding the employee!", error);
      });
  };

  return (
    <div className={styles.employeeAddFormPage}>
      <h1 className={styles.title}>Add New Employee</h1>
      {error && <div className={styles.error}>Error: {error.message}</div>}
      <form onSubmit={handleSubmit} className={styles.form}>
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
          <input
            type="text"
            name="level"
            value={formData.level}
            onChange={handleChange}
            required
            className={styles.input}
          />
        </label>
        <label className={styles.label}>
          First Name:
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            required
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
            required
            className={styles.input}
          />
        </label>
        <label className={styles.label}>
          Email:
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
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
            required
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
        <button type="submit" className={styles.button}>
          Submit
        </button>
      </form>
    </div>
  );
};

export default EmployeeAddFormPage;
