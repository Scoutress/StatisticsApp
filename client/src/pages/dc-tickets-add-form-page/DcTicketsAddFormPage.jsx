/* eslint-disable react-hooks/exhaustive-deps */
import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "./DcTicketsAddFormPage.module.scss";

const DcTicketsAddFormPage = () => {
  const [employees, setEmployees] = useState([]);
  const [formData, setFormData] = useState({ date: "", tickets: [] });
  const navigate = useNavigate();

  useEffect(() => {
    axios
      .get("http://localhost:8080/employee/all")
      .then((response) => {
        setEmployees(response.data);
        const initialTickets = response.data.map((employee) => ({
          employeeId: employee.id,
          ticketCount: "",
        }));
        setFormData({ ...formData, tickets: initialTickets });
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
    <form onSubmit={handleSubmit} className={styles.form}>
      <label className={styles.label}>
        Date:
        <input
          type="date"
          name="date"
          value={formData.date}
          onChange={(e) => handleChange(e)}
          required
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
  );
};

export default DcTicketsAddFormPage;
