/* eslint-disable react-hooks/exhaustive-deps */
import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "./PlaytimeAddFormPage.module.scss";

const PlaytimeAddFormPage = () => {
  const [formData, setFormData] = useState({
    date: "",
    playtimes: [],
  });
  const [employees, setEmployees] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const today = new Date().toISOString().split("T")[0];
    setFormData((prevData) => ({
      ...prevData,
      date: today,
    }));

    axios
      .get("http://localhost:8080/employee/all")
      .then((response) => {
        const sortedEmployees = response.data.sort((a, b) => a.id - b.id);
        setEmployees(sortedEmployees);
        const initialPlaytimes = sortedEmployees.map((employee) => ({
          employeeId: employee.id,
          hoursPlayed: "",
        }));
        setFormData((prevData) => ({
          ...prevData,
          playtimes: initialPlaytimes,
        }));
      })
      .catch((error) => setError(error));
  }, []);

  const handleChange = (e, employeeId) => {
    const { name, value } = e.target;
    if (name === "date") {
      setFormData({ ...formData, date: value });
    } else {
      const updatedPlaytimes = formData.playtimes.map((playtime) =>
        playtime.employeeId === employeeId
          ? { ...playtime, hoursPlayed: value }
          : playtime
      );
      setFormData({ ...formData, playtimes: updatedPlaytimes });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = formData.playtimes.map((playtime) => ({
      employeeId: playtime.employeeId,
      date: formData.date,
      hoursPlayed: playtime.hoursPlayed,
    }));
    axios
      .post("http://localhost:8080/playtime/add", payload)
      .then((response) => {
        console.log("Playtime data added", response);
        navigate("/playtime/all");
      })
      .catch((error) => console.error(error));
  };

  return (
    <div className={styles.playtimeAddFormPage}>
      <h1 className={styles.title}>Add Playtime Data</h1>
      {error && <div className={styles.error}>Error: {error.message}</div>}
      <form onSubmit={handleSubmit} className={styles.form}>
        <label className={styles.label}>
          Date:
          <input
            type="text"
            name="date"
            value={formData.date}
            onChange={handleChange}
            required
            pattern="\d{4}-\d{2}-\d{2}"
            placeholder="yyyy-mm-dd"
            className={styles.input}
          />
        </label>
        {employees.map((employee) => (
          <div key={employee.id} className={styles.inputGroup}>
            <label className={styles.label}>
              {employee.username}:
              <input
                type="number"
                name={`hoursPlayed-${employee.id}`}
                value={
                  formData.playtimes.find(
                    (playtime) => playtime.employeeId === employee.id
                  )?.hoursPlayed || ""
                }
                onChange={(e) => handleChange(e, employee.id)}
                required
                className={styles.input}
                min="0"
                step="0.1"
              />
            </label>
          </div>
        ))}
        <button type="submit" className={styles.button}>
          Submit
        </button>
      </form>
    </div>
  );
};

export default PlaytimeAddFormPage;
