import React from 'react';

function Statistics() {
  const styles = {
    table: {
      width: '100%',
      borderCollapse: 'collapse',
    },
    th: {
      background: '#007bff',
      color: 'white',
      padding: '10px',
      border: '1px solid #ddd',
    },
    td: {
      padding: '8px',
      border: '1px solid #ddd',
      textAlign: 'center',
    }
  };

  return (
    <div>
      <h2>Statistics</h2>
      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Lygis</th>
            <th style={styles.th}>Slapyvardis</th>
            <th style={styles.th}>Aktyv/pusm</th>
            <th style={styles.th}>Pagalbos</th>
            <th style={styles.th}>Priima pagalbas</th>
            <th style={styles.th}>Aktyvumas dc</th>
            <th style={styles.th}>Procentaliai dc</th>
            <th style={styles.th}>Ingame laikas</th>
            <th style={styles.th}>Ingame AFK</th>
            <th style={styles.th}>Produktyvumas</th>
            <th style={styles.th}>Rekomendacija</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style={styles.td}>1</td>
            <td style={styles.td}>Slapyvardis1</td>
            <td style={styles.td}>Aktyvus</td>
            <td style={styles.td}>5</td>
            <td style={styles.td}>10</td>
            <td style={styles.td}>2</td>
            <td style={styles.td}>20%</td>
            <td style={styles.td}>2 val.</td>
            <td style={styles.td}>15 min.</td>
            <td style={styles.td}>Labai produktyvus</td>
            <td style={styles.td}>Gera</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default Statistics;
