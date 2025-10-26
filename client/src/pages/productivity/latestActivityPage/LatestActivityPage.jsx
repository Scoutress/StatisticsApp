import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./LatestActivityPage.module.scss";

const startDates = [
  "2023-07-01", // Discord Chat
  "2024-04-20", // Discord Ticket
  "2023-06-02", // Minecraft Helpop
  "2023-05-28", // Playtime
];

const endpoints = [
  {
    url: "http://localhost:8080/statistics/latest-activity/dc-chat",
    title: "Days Since Last Discord Chat",
    valueKey: "daysSinceLastDiscordChat",
  },
  {
    url: "http://localhost:8080/statistics/latest-activity/dc-ticket",
    title: "Days Since Last Discord Ticket",
    valueKey: "daysSinceLastDiscordTicket",
  },
  {
    url: "http://localhost:8080/statistics/latest-activity/mc-ticket",
    title: "Days Since Last Minecraft Helpop",
    valueKey: "daysSinceLastMinecraftTicket",
  },
  {
    url: "http://localhost:8080/statistics/latest-activity/playtime",
    title: "Days Since Last Playtime",
    valueKey: "daysSinceLastPlaytime",
  },
];

const getDaysSince = (dateStr) => {
  const start = new Date(dateStr);
  const now = new Date();
  return Math.floor((now - start) / (1000 * 60 * 60 * 24));
};

const LatestActivityPage = () => {
  const [tables, setTables] = useState([[], [], [], []]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    Promise.all(endpoints.map((e) => axios.get(e.url)))
      .then((responses) => {
        setTables(responses.map((r) => r.data));
      })
      .catch((err) => setError("Error loading data: " + err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className={styles.loading}>Loading...</div>;
  if (error) return <div className={styles.error}>{error}</div>;

  return (
    <div className={styles.latestActivityPage}>
      {endpoints.map((endpoint, idx) => {
        let helpopDays = null;
        if (
          endpoint.valueKey === "daysSinceLastMinecraftTicket" &&
          tables[idx].length > 0
        ) {
          const validDays = tables[idx]
            .map((item) => item[endpoint.valueKey])
            .filter((val) => val !== -1);
          if (validDays.length > 0) {
            helpopDays = Math.min(...validDays);
          }
        }

        const sinceDate = startDates[idx];
        const sinceDays = getDaysSince(sinceDate);

        return (
          <div className={styles.tableContainer} key={endpoint.url}>
            <h3>
              {endpoint.title}
              {endpoint.valueKey === "daysSinceLastMinecraftTicket" &&
                helpopDays !== null}
            </h3>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Position</th>
                  <th>Username</th>
                  <th>Days</th>
                </tr>
              </thead>
              <tbody>
                {tables[idx].map((item, i) => {
                  const isNoDate = item[endpoint.valueKey] === -1;
                  return (
                    <tr
                      key={item.employeeId}
                      className={isNoDate ? styles.noDateRow : undefined}
                    >
                      <td>{i + 1}</td>
                      <td>{item.username}</td>
                      <td>{isNoDate ? "n/d" : item[endpoint.valueKey]}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            <div className={styles.sinceNote}>
              Data since {sinceDate} ({sinceDays} days)
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default LatestActivityPage;
