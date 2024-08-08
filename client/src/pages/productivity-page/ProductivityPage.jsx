import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./ProductivityPage.module.scss";

const ProductivityPage = () => {
  const [productivityData, setProductivityData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      axios
        .get("http://localhost:8080/stats/productivity")
        .then((response) => {
          setProductivityData(response.data);
          setIsLoading(false);
        })
        .catch((error) => {
          setError(error);
          setIsLoading(false);
        });
    };
    fetchData();
  }, []);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>There was an error fetching the data! {error.message}</div>;
  }

  return (
    <div className={styles.productivityPage}>
      <h1 className={styles.title}>Productivity Statistics</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Level</th>
            <th>Annual Playtime</th>
            <th>Server Tickets</th>
            <th>Server Tickets Taking</th>
            <th>Discord Tickets</th>
            <th>Discord Tickets Taking</th>
            <th>Playtime</th>
            <th>Afk Playtime</th>
            <th>Productivity</th>
            <th>Recommendation</th>
          </tr>
        </thead>
        <tbody>
          {productivityData.map((item) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.employee ? item.employee.username : "N/A"}</td>
              <td>{item.employee ? item.employee.level : "N/A"}</td>
              <td>{item.annualPlaytime}</td>
              <td>{item.serverTickets}</td>
              <td>{item.serverTicketsTaking}</td>
              <td>{item.discordTickets}</td>
              <td>{item.discordTicketsTaking}</td>
              <td>{item.playtime}</td>
              <td>{item.afkPlaytime}</td>
              <td>{item.productivity}</td>
              <td>{item.recommendation}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductivityPage;
