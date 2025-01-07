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

  const hardcodedData = [
    {
      //ItsVaidas
      id: 1,
      annualPlaytime: 1057,
    },
    {
      //Scoutress
      id: 2,
      annualPlaytime: 1374,
    },
    {
      //Mboti212
      id: 3,
      annualPlaytime: 443.29,
      serverTickets: 1.46,
      serverTicketsTaking: 41.85,
      playtime: 1.82,
      afkPlaytime: 0,
      productivity: "47.51%",
      recommendation: "-",
    },
    {
      //Furija
      id: 4,
      annualPlaytime: 559.08,
      serverTickets: 0.93,
      serverTicketsTaking: 23.93,
      playtime: 0.72,
      afkPlaytime: 0,
      productivity: "41.41%",
      recommendation: "-",
    },
    {
      //Ernestasltu12
      id: 5,
      annualPlaytime: 470.72,
      serverTickets: 1.51,
      serverTicketsTaking: 22.57,
      playtime: 1.08,
      afkPlaytime: 0,
      productivity: "81.46%",
      recommendation: "-",
    },
    {
      //D0fka
      id: 6,
      annualPlaytime: 111.52,
      serverTickets: 0.03,
      serverTicketsTaking: 4.08,
      playtime: 0.29,
      afkPlaytime: 0,
      productivity: "42.95%",
      recommendation: "-",
    },
    {
      //MelitaLove
      id: 7,
      annualPlaytime: 429.14,
      serverTickets: 0.32,
      serverTicketsTaking: 24.53,
      playtime: 0.67,
      afkPlaytime: 0,
      productivity: "35.39%",
      recommendation: "-",
    },
    {
      //Libete
      id: 8,
      annualPlaytime: 308.72,
      serverTickets: 0.22,
      serverTicketsTaking: 36.12,
      playtime: 0.56,
      afkPlaytime: 0,
      productivity: "31.53%",
      recommendation: "-",
    },
    {
      //Ariena
      id: 9,
      annualPlaytime: 252.34,
      serverTickets: 0.33,
      serverTicketsTaking: 21.2,
      playtime: 0.52,
      afkPlaytime: 0,
      productivity: "26.32%",
      recommendation: "-",
    },
    {
      //RichPica
      id: 10,
      annualPlaytime: 553.23,
      serverTickets: 1.53,
      serverTicketsTaking: 33.33,
      playtime: 0.99,
      afkPlaytime: 0,
      productivity: "53.16%",
      recommendation: "-",
    },
    {
      //plrxq
      id: 11,
      annualPlaytime: 696.87,
      serverTickets: 0.77,
      serverTicketsTaking: 28.68,
      playtime: 0.64,
      afkPlaytime: 0,
      productivity: "63.81%",
      recommendation: "-",
    },
    {
      //Beche_
      id: 12,
      annualPlaytime: 113.64,
      serverTickets: 0.38,
      serverTicketsTaking: 8.66,
      playtime: 0.33,
      afkPlaytime: 0,
      productivity: "30.23%",
      recommendation: "-",
    },
    {
      //everly
      id: 13,
      annualPlaytime: 185.37,
      serverTickets: 0.4,
      serverTicketsTaking: 7.74,
      playtime: 0.4,
      afkPlaytime: 0,
      productivity: "35.59%",
      recommendation: "-",
    },
    {
      //Shizo
      id: 14,
      annualPlaytime: 46.61,
      serverTickets: 0.16,
      serverTicketsTaking: 5,
      playtime: 0.39,
      afkPlaytime: 0,
      productivity: "38.88%",
      recommendation: "-",
    },
    {
      //3MAHH
      id: 15,
      annualPlaytime: 174.32,
      serverTickets: 0,
      serverTicketsTaking: 0,
      playtime: 0.0041,
      afkPlaytime: 0,
      productivity: "36.9%",
      recommendation: "-",
    },
    {
      //BobsBuilder
      id: 16,
      annualPlaytime: 41.29,
      serverTickets: 0,
      serverTicketsTaking: 0,
      playtime: 0.0012,
      afkPlaytime: 0,
      productivity: "42.03%",
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
              <td>{item.level}</td>
              <td>{item.username}</td>
              <td>{item.annualPlaytime.toFixed(2)}</td>
              <td>{item.minecraftTickets.toFixed(2)}</td>
              <td>{(item.minecraftTicketsCompared * 100).toFixed(2)}</td>
              <td>{item.discordMessages.toFixed(2)}</td>
              <td>{(item.discordMessagesCompared * 100).toFixed(2)}</td>
              <td>{item.playtime.toFixed(2)}</td>
              <td>{(item.productivity * 100).toFixed(2)}</td>
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
              <td>{(item.minecraftTicketsCompared * 100).toFixed(2)}</td>
              <td>{hardcodedData[index].serverTicketsTaking}</td>
              <td>{item.playtime.toFixed(2)}</td>
              <td>{hardcodedData[index].playtime}</td>
              <td>{(item.productivity * 100).toFixed(2)}</td>
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
