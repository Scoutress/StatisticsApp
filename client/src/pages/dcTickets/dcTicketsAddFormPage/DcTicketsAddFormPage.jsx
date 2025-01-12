/* eslint-disable react-hooks/exhaustive-deps */
import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "./DcTicketsAddFormPage.module.scss";

const DcTicketsAddFormPage = () => {
  const [formData, setFormData] = useState({ date: "", tickets: [] });
  const [employees, setEmployees] = useState([]);
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
        const initialTickets = response.data.map((employee) => ({
          employeeId: employee.id,
          ticketCount: "",
        }));
        setFormData((prevData) => ({
          ...prevData,
          tickets: initialTickets,
        }));
      })
      .catch((error) => console.error(error));
  }, []);

  const handleChange = (e, employeeId) => {
    const { name, value } = e.target;
    if (name === "date") {
      setFormData({ ...formData, date: value });
    } else {
      const updatedTickets = formData.tickets.map((ticket) =>
        ticket.employeeId === employeeId
          ? { ...ticket, ticketCount: value }
          : ticket
      );
      setFormData({ ...formData, tickets: updatedTickets });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = formData.tickets.map((ticket) => ({
      employeeId: ticket.employeeId,
      date: formData.date,
      ticketCount: ticket.ticketCount,
    }));
    axios
      .post("http://localhost:8080/dc-tickets/add", payload)
      .then((response) => {
        console.log("DcTickets added", response);
        navigate("/dc-tickets/all");
      })
      .catch((error) => console.error(error));
  };

  return (
    <div>
      <h1>Add dc tickets</h1>
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
                name={`ticketCount-${employee.id}`}
                value={
                  formData.tickets.find(
                    (ticket) => ticket.employeeId === employee.id
                  )?.ticketCount || ""
                }
                onChange={(e) => handleChange(e, employee.id)}
                required
                className={styles.input}
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

export default DcTicketsAddFormPage;
