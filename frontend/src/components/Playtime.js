import React from 'react';

const Playtime = () => {
  const data = [
    { metai: '2024', savaitė: '7', itsVaidas: '25', scoutress: '15' },
    { metai: '2024', savaitė: '8', itsVaidas: '30', scoutress: '20' },
    { metai: '2024', savaitė: '9', itsVaidas: '28', scoutress: '18' },
  ];

  const styles = {
    table: {
      width: '100%',
      borderCollapse: 'collapse',
    },
    th: {
      background: '#4CAF50',
      color: 'white',
      padding: '8px',
      border: '1px solid #ddd',
      textAlign: 'center',
    },
    td: {
      padding: '8px',
      border: '1px solid #ddd',
      textAlign: 'center',
    },
  };

  return (
    <div>
      <h2>Playtime</h2>
      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Metai</th>
            <th style={styles.th}>Savaitė</th>
            <th style={styles.th}>ItsVaidas</th>
            <th style={styles.th}>Scoutress</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr key={index}>
              <td style={styles.td}>{row.metai}</td>
              <td style={styles.td}>{row.savaitė}</td>
              <td style={styles.td}>{row.itsVaidas}</td>
              <td style={styles.td}>{row.scoutress}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Playtime;
