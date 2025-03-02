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
      serverTickets: 1.45,
      serverTicketsTaking: 36.36,
      discordMessages: 5.99,
      discordMessagesCompared: 13.14,
      playtime: 2.05,
      afkPlaytime: 0,
      productivity: "47.93%",
      recommendation: "-",
    },
    {
      //Furija
      id: 4,
      annualPlaytime: 547.61,
      serverTickets: 0.92,
      serverTicketsTaking: 24.41,
      discordMessages: 7.16,
      discordMessagesCompared: 14.99,
      playtime: 1.27,
      afkPlaytime: 0,
      productivity: "42.54%",
      recommendation: "-",
    },
    {
      //Ernestasltu12
      id: 5,
      annualPlaytime: 467.61,
      serverTickets: 1.5,
      serverTicketsTaking: 23.82,
      discordMessages: 14.9,
      discordMessagesCompared: 30.82,
      playtime: 1.7,
      afkPlaytime: 0,
      productivity: "83.96%",
      recommendation: "-",
    },
    {
      //D0fka
      id: 6,
      annualPlaytime: 110.68,
      serverTickets: 0.03,
      serverTicketsTaking: 1.33,
      discordMessages: 3.69,
      discordMessagesCompared: 5.96,
      playtime: 0.46,
      afkPlaytime: 0,
      productivity: "43.65%",
      recommendation: "-",
    },
    {
      //MelitaLove
      id: 7,
      annualPlaytime: 429.14,
      serverTickets: 0.31,
      serverTicketsTaking: 18.18,
      discordMessages: 1.55,
      discordMessagesCompared: 4.29,
      playtime: 1.02,
      afkPlaytime: 0,
      productivity: "36.83%",
      recommendation: "-",
    },
    {
      //Libete
      id: 8,
      annualPlaytime: 308.72,
      serverTickets: 0.21,
      serverTicketsTaking: 18.59,
      discordMessages: 0.26,
      discordMessagesCompared: 2.17,
      playtime: 0.59,
      afkPlaytime: 0,
      productivity: "31.64%",
      recommendation: "-",
    },
    {
      //Ariena
      id: 9,
      annualPlaytime: 244.47,
      serverTickets: 0.32,
      serverTicketsTaking: 15.95,
      discordMessages: 0.07,
      discordMessagesCompared: 0.3,
      playtime: 0.6,
      afkPlaytime: 0,
      productivity: "26.59%",
      recommendation: "-",
    },
    {
      //RichPica
      id: 10,
      annualPlaytime: 495.45,
      serverTickets: 1.51,
      serverTicketsTaking: 35.42,
      discordMessages: 2.52,
      discordMessagesCompared: 6.69,
      playtime: 2.5,
      afkPlaytime: 0,
      productivity: "59.30%",
      recommendation: "-",
    },
    {
      //plrxq
      id: 11,
      annualPlaytime: 696.87,
      serverTickets: 0.76,
      serverTicketsTaking: 28.64,
      discordMessages: 10.78,
      discordMessagesCompared: 25.29,
      playtime: 1.8,
      afkPlaytime: 0,
      productivity: "68.50%",
      recommendation: "-",
    },
    {
      //Beche_
      id: 12,
      annualPlaytime: 113.01,
      serverTickets: 0.38,
      serverTicketsTaking: 12.76,
      discordMessages: 0.03,
      discordMessagesCompared: 0.04,
      playtime: 0.52,
      afkPlaytime: 0,
      productivity: "31.76%",
      recommendation: "-",
    },
    {
      //everly
      id: 13,
      annualPlaytime: 180.11,
      serverTickets: 0.4,
      serverTicketsTaking: 12.58,
      discordMessages: 0.81,
      discordMessagesCompared: 1.23,
      playtime: 0.93,
      afkPlaytime: 0,
      productivity: "39.94%",
      recommendation: "-",
    },
    {
      //Shizo
      id: 14,
      annualPlaytime: 46.61,
      serverTickets: 0.16,
      serverTicketsTaking: 19.02,
      discordMessages: 0.34,
      discordMessagesCompared: 0.67,
      playtime: 0.58,
      afkPlaytime: 0,
      productivity: "42.07%",
      recommendation: "-",
    },
    {
      //3MAHH
      id: 15,
      annualPlaytime: 174.32,
      serverTickets: 0,
      serverTicketsTaking: 0,
      discordMessages: 0.85,
      discordMessagesCompared: 2.79,
      playtime: 0.42,
      afkPlaytime: 0,
      productivity: "43.86%",
      recommendation: "-",
    },
    {
      //BobsBuilder
      id: 16,
      annualPlaytime: 41.29,
      serverTickets: 0,
      serverTicketsTaking: 0,
      discordMessages: 0.44,
      discordMessagesCompared: 0.58,
      playtime: 0.12,
      afkPlaytime: 0,
      productivity: "47.97%",
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
            <th>Discord messages /day</th>
            <th>Discord messages /day (TEST)</th>
            <th>Discord messages compared %/day</th>
            <th>Discord messages compared %/day (TEST)</th>
            <th>Playtime hours/day</th>
            <th>Playtime hours/day (TEST)</th>
            <th>Productivity %</th>
            <th>Productivity % (TEST)</th>
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
              <td>{item.discordMessages.toFixed(2)}</td>
              <td>{hardcodedData[index].discordMessages}</td>
              <td>{(item.discordMessagesCompared * 100).toFixed(2)}</td>
              <td>{hardcodedData[index].discordMessagesCompared}</td>
              <td>{item.playtime.toFixed(2)}</td>
              <td>{hardcodedData[index].playtime}</td>
              <td>{(item.productivity * 100).toFixed(2)}</td>
              <td>{hardcodedData[index].productivity}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductivityPage;
