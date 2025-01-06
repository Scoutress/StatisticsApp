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
      annualPlaytime: 443,
      serverTickets: 1.8,
      serverTicketsTaking: 52,
      playtime: 0.99,
      afkPlaytime: 0,
      productivity: "50%",
      recommendation: "-",
    },
    {
      //Furija
      id: 4,
      annualPlaytime: 564,
      serverTickets: 0.87,
      serverTicketsTaking: 18.3,
      playtime: 0.96,
      afkPlaytime: 0,
      productivity: "41%",
      recommendation: "-",
    },
    {
      //Ernestasltu12
      id: 5,
      annualPlaytime: 475,
      serverTickets: 1.93,
      serverTicketsTaking: 27.56,
      playtime: 1.2,
      afkPlaytime: 0,
      productivity: "86.4%",
      recommendation: "-",
    },
    {
      //D0fka
      id: 6,
      annualPlaytime: 111,
      serverTickets: 0.04,
      serverTicketsTaking: 4.4,
      playtime: 0.28,
      afkPlaytime: 0,
      productivity: "46%",
      recommendation: "-",
    },
    {
      //MelitaLove
      id: 7,
      annualPlaytime: 429,
      serverTickets: 0.32,
      serverTicketsTaking: 10.2,
      playtime: 0.49,
      afkPlaytime: 0,
      productivity: "30.4%",
      recommendation: "-",
    },
    {
      //Libete
      id: 8,
      annualPlaytime: 309,
      serverTickets: 0.15,
      serverTicketsTaking: 1.7,
      playtime: 0.05,
      afkPlaytime: 0,
      productivity: "19.6%",
      recommendation: "-",
    },
    {
      //Ariena
      id: 9,
      annualPlaytime: 254,
      serverTickets: 0.33,
      serverTicketsTaking: 5.4,
      playtime: 0.17,
      afkPlaytime: 0,
      productivity: "21.35%",
      recommendation: "-",
    },
    {
      //RichPica
      id: 10,
      annualPlaytime: 565,
      serverTickets: 0.77,
      serverTicketsTaking: 13,
      playtime: 0.15,
      afkPlaytime: 0,
      productivity: "43.4%",
      recommendation: "-",
    },
    {
      //plrxq
      id: 11,
      annualPlaytime: 697,
      serverTickets: 2.3,
      serverTicketsTaking: 36,
      playtime: 2.3,
      afkPlaytime: 0,
      productivity: "70%",
      recommendation: "-",
    },
    {
      //Beche_
      id: 12,
      annualPlaytime: 122,
      serverTickets: 0.5,
      serverTicketsTaking: 9,
      playtime: 0.41,
      afkPlaytime: 0,
      productivity: "33.3%",
      recommendation: "-",
    },
    {
      //everly
      id: 13,
      annualPlaytime: 188,
      serverTickets: 0.57,
      serverTicketsTaking: 9,
      playtime: 0.77,
      afkPlaytime: 0,
      productivity: "44%",
      recommendation: "-",
    },
    {
      //Shizo
      id: 14,
      annualPlaytime: 46,
      serverTickets: 1.2,
      serverTicketsTaking: 27,
      playtime: 2.28,
      afkPlaytime: 0,
      productivity: "95%",
      recommendation: "-",
    },
    {
      //3MAHH
      id: 15,
      annualPlaytime: 174,
      serverTickets: 0.21,
      serverTicketsTaking: 0,
      playtime: 0.08,
      afkPlaytime: 0,
      productivity: "32%",
      recommendation: "-",
    },
    {
      //BobsBuilder
      id: 16,
      annualPlaytime: 41,
      serverTickets: 0,
      serverTicketsTaking: 0,
      playtime: 0.21,
      afkPlaytime: 0,
      productivity: "30%",
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
              <td>{(item.minecraftTicketsCompared * 100).toFixed(2)}</td>
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
