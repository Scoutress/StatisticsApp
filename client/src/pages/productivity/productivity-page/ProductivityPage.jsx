import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../../components/loading/Loading.jsx";
import ErrorMessage from "../../../components/errorMessage/ErrorMessage.jsx";
import styles from "./ProductivityPage.module.scss";

const ProductivityPage = () => {
  const [productivityData, setProductivityData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const runTasks = async () => {
    setLoading(true);
    setError(null);
    try {
      await fetch("http://localhost:8080/stats/update", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
      });
    } catch (error) {
      setError("There was an error fetching the data! " + error.message);
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    const fetchData = async () => {
      setError(null);
      try {
        const response = await axios.get(
          "http://localhost:8080/stats/productivity"
        );
        setProductivityData(response.data || []);
      } catch (error) {
        setError("There was an error fetching the data! " + error.message);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <div>{error && <ErrorMessage message={error} />}</div>;
  }

  const hardcodedData = [
    {
      id: 1,
      annualPlaytime: 1181,
    },
    {
      id: 2,
      annualPlaytime: 1201,
    },
    {
      id: 3,
      annualPlaytime: 710,
      serverTickets: 1.8,
      serverTicketsTaking: 52,
      playtime: 0.99,
      afkPlaytime: 0,
      productivity: "50%",
      recommendation: "-",
    },
    {
      id: 4,
      annualPlaytime: 407,
      serverTickets: 0.87,
      serverTicketsTaking: 18.3,
      playtime: 0.96,
      afkPlaytime: 0,
      productivity: "41%",
      recommendation: "-",
    },
    {
      id: 5,
      annualPlaytime: 726,
      serverTickets: 1.93,
      serverTicketsTaking: 27.56,
      playtime: 1.2,
      afkPlaytime: 0,
      productivity: "86.4%",
      recommendation: "-",
    },
    {
      id: 6,
      annualPlaytime: 256,
      serverTickets: 0.04,
      serverTicketsTaking: 4.4,
      playtime: 0.28,
      afkPlaytime: 0,
      productivity: "46%",
      recommendation: "-",
    },
    {
      id: 7,
      annualPlaytime: 387,
      serverTickets: 0.32,
      serverTicketsTaking: 10.2,
      playtime: 0.49,
      afkPlaytime: 0,
      productivity: "30.4%",
      recommendation: "-",
    },
    {
      id: 8,
      annualPlaytime: 51,
      serverTickets: 0.15,
      serverTicketsTaking: 1.7,
      playtime: 0.05,
      afkPlaytime: 0,
      productivity: "19.6%",
      recommendation: "-",
    },
    {
      id: 9,
      annualPlaytime: 130,
      serverTickets: 0.33,
      serverTicketsTaking: 5.4,
      playtime: 0.17,
      afkPlaytime: 0,
      productivity: "21.35%",
      recommendation: "-",
    },
    {
      id: 10,
      annualPlaytime: 182,
      serverTickets: 0.77,
      serverTicketsTaking: 13,
      playtime: 0.15,
      afkPlaytime: 0,
      productivity: "43.4%",
      recommendation: "-",
    },
    {
      id: 11,
      annualPlaytime: 986,
      serverTickets: 2.3,
      serverTicketsTaking: 36,
      playtime: 2.3,
      afkPlaytime: 0,
      productivity: "70%",
      recommendation: "-",
    },
    {
      id: 12,
      annualPlaytime: 208,
      serverTickets: 0.5,
      serverTicketsTaking: 9,
      playtime: 0.41,
      afkPlaytime: 0,
      productivity: "33.3%",
      recommendation: "-",
    },
    {
      id: 13,
      annualPlaytime: 456,
      serverTickets: 0.57,
      serverTicketsTaking: 9,
      playtime: 0.77,
      afkPlaytime: 0,
      productivity: "44%",
      recommendation: "-",
    },
    {
      id: 14,
      annualPlaytime: 511,
      serverTickets: 1.2,
      serverTicketsTaking: 27,
      playtime: 2.28,
      afkPlaytime: 0,
      productivity: "95%",
      recommendation: "-",
    },
    {
      id: 15,
      annualPlaytime: 297,
      serverTickets: 0.21,
      serverTicketsTaking: 0,
      playtime: 0.08,
      afkPlaytime: 0,
      productivity: "32%",
      recommendation: "-",
    },
    {
      id: 16,
      annualPlaytime: 41,
      serverTickets: 0,
      serverTicketsTaking: 0,
      playtime: 0.21,
      afkPlaytime: 0,
      productivity: "30%",
      recommendation: "-",
    },
    {
      id: 17,
      annualPlaytime: 174,
      serverTickets: 0,
      serverTicketsTaking: 0,
      playtime: 0.8,
      afkPlaytime: 0,
      productivity: "56%",
      recommendation: "-",
    },
  ];

  return (
    <div className={styles.productivityPage}>
      <h1 className={styles.title}>Productivity Statistics</h1>

      <button onClick={runTasks} disabled={loading}>
        {"Update data"}
      </button>
      {loading && (
        <div className="spinner">
          <p>Updating...</p>
        </div>
      )}

      <table className={styles.table}>
        <thead>
          <tr>
            <th>Level</th>
            <th>Username</th>
            <th>Annual playtime, hours/year</th>
            <th>Minecraft tickets, tickets/day</th>
            <th>Minecraft tickets compared, %/day</th>
            <th>Playtime, hours/day</th>
            <th>Productivity, %</th>
            <th>Recommendation</th>
          </tr>
        </thead>
        <tbody>
          {productivityData.map((item) => (
            <tr key={item.id}>
              <td>{item.level}</td>
              <td>{item.username}</td>
              <td>{item.annualPlaytime.toFixed(2)}</td>
              <td>{item.minecraftTickets.toFixed(2)}</td>
              <td>{item.minecraftTicketsCompared.toFixed(2)}</td>
              <td>{item.playtime.toFixed(2)}</td>
              <td>{item.productivity.toFixed(2)}</td>
              <td>{item.recommendation}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <h1 className={styles.title}>
        Productivity Statistics (for testing purposes)
      </h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Level</th>
            <th>Username</th>
            <th>Annual playtime</th>
            <th>Annual playtime (TEST)</th>
            <th>Minecraft tickets /day</th>
            <th>Minecraft tickets /day (TEST)</th>
            <th>Minecraft tickets compared %/day</th>
            <th>Minecraft tickets compared %/day (TEST)</th>
            <th>Playtime hours/day</th>
            <th>Playtime hours/day (TEST)</th>
            <th>Productivity %</th>
            <th>Productivity % (TEST)</th>
            <th>Recommendation</th>
            <th>Recommendation (TEST)</th>
          </tr>
        </thead>
        <tbody>
          {productivityData.map((item, index) => (
            <tr key={item.id}>
              <td>{item.level}</td>
              <td>{item.username}</td>
              <td>{item.annualPlaytime.toFixed(2)}</td>
              <td>{hardcodedData[index].annualPlaytime}</td>
              <td>{item.minecraftTickets.toFixed(2)}</td>
              <td>{hardcodedData[index].serverTickets}</td>
              <td>{item.minecraftTicketsCompared.toFixed(2)}</td>
              <td>{hardcodedData[index].serverTicketsTaking}</td>
              <td>{item.playtime.toFixed(2)}</td>
              <td>{hardcodedData[index].playtime}</td>
              <td>{item.productivity.toFixed(2)}</td>
              <td>{hardcodedData[index].productivity}</td>
              <td>{item.recommendation}</td>
              <td>{hardcodedData[index].recommendation}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductivityPage;
