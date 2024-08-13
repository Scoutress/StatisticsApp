import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./PromotionsPage.module.scss";

const PromotionsPage = () => {
  const [promotionsData, setPromotionsData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      axios
        .get("http://localhost:8080/stats/promotions")
        .then((response) => {
          setPromotionsData(response.data);
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
    <div className={styles.page}>
      <h1 className={styles.title}>Employee Promotions</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Username</th>
            <th>Level</th>
            <th>To Support</th>
            <th>To ChatMod</th>
            <th>To Overseer</th>
            <th>To Manager</th>
          </tr>
        </thead>
        <tbody>
          {promotionsData.map((item) => (
            <tr key={item.employeeId}>
              <td>{item.username ? item.username : "N/A"}</td>
              <td>{item.level ? item.level : "N/A"}</td>
              <td>{item.toSupport ? item.toSupport : "N/A"}</td>
              <td>{item.toChatmod ? item.toChatmod : "N/A"}</td>
              <td>{item.toOverseer ? item.toOverseer : "N/A"}</td>
              <td>{item.toManager ? item.toManager : "N/A"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default PromotionsPage;
