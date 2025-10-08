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

      const sortByLevel = (data) => {
        return data.sort((a, b) => {
          return levelOrder.indexOf(a.level) - levelOrder.indexOf(b.level);
        });
      };

      const levelOrder = [
        "Owner",
        "Operator",
        "Manager",
        "Organizer",
        "Overseer",
        "ChatMod",
        "Support",
        "Helper",
      ];

      try {
        const response = await axios.get(
          "http://localhost:8080/stats/productivity"
        );
        const sortedData = sortByLevel(response.data || []);
        setProductivityData(sortedData);
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
            <th>Discord messages, msg/day</th>
            <th>Discord messages compared, %/day</th>
            <th>Playtime, hours/day</th>
            <th>Productivity, %</th>
            <th>Recommendation</th>
          </tr>
        </thead>
        <tbody>
          {productivityData.map((item) => (
            <tr key={item.id}>
              <td>{item.level ?? "-"}</td>
              <td>{item.username ?? "-"}</td>
              <td>{item.annualPlaytime?.toFixed(2) ?? "-"}</td>
              <td>{item.minecraftTickets?.toFixed(2) ?? "-"}</td>
              <td>
                {item.minecraftTicketsCompared != null
                  ? (item.minecraftTicketsCompared * 100).toFixed(2)
                  : "-"}
              </td>
              <td>{item.discordMessages?.toFixed(2) ?? "-"}</td>
              <td>
                {item.discordMessagesCompared != null
                  ? (item.discordMessagesCompared * 100).toFixed(2)
                  : "-"}
              </td>
              <td>{item.playtime?.toFixed(2) ?? "-"}</td>
              <td>
                {item.productivity != null
                  ? (item.productivity * 100).toFixed(2)
                  : "-"}
              </td>
              <td>{item.recommendation ?? "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductivityPage;
