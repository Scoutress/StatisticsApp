import styles from "./ProductivityPage.module.scss";

const ProductivityPage = () => {
  const columnHeaders = [
    "Level",
    "Username",
    "Annual Playtime",
    "Server tickets",
    "Server tickets taking",
    "Discord tickets",
    "Discord tickets taking",
    "Playtime",
    "Afk playtime",
    "Productivity",
    "Recomendation",
  ];

  const tableData = Array.from({ length: 15 }, (_, rowIndex) => (
    <tr key={rowIndex}>
      {Array.from({ length: columnHeaders.length }, (_, colIndex) => (
        <td key={colIndex}>
          Cell {rowIndex + 1},{colIndex + 1}
        </td>
      ))}
    </tr>
  ));

  return (
    <div className={styles.productivityPage}>
      <h1 className={styles.title}>Productivity Statistics</h1>
      <table className={styles.table}>
        <thead>
          <tr>
            {columnHeaders.map((header, index) => (
              <th key={index}>{header}</th>
            ))}
          </tr>
        </thead>
        <tbody>{tableData}</tbody>
      </table>
    </div>
  );
};

export default ProductivityPage;
