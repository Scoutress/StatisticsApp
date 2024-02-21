import React from 'react';

const HelpRequests = () => {
  const data = [
    {
      data: '2024-02-21',
      metai: '2024',
      menuo: '02',
      diena: '21',
      kodas: 'AB123',
      '1': 'Taip',
      '2': 'Ne',
      '3': 'Ne',
      '4': 'Ne',
      '5': 'Ne',
      '6': 'Ne',
      '7': 'Ne',
      '8': 'Ne',
    },
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
      textAlign: 'left',
    },
  };

  return (
    <div>
      <h2>Help Requests</h2>
      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Data</th>
            <th style={styles.th}>Metai</th>
            <th style={styles.th}>Mėnuo</th>
            <th style={styles.th}>Diena</th>
            <th style={styles.th}>Kodas</th>
            <th style={styles.th} colSpan="4">Mboti212</th>
            <th style={styles.th} colSpan="4">D0fka</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr key={index}>
            <td style={styles.td}>{row.data}</td>
            <td style={styles.td}>{row.metai}</td>
            <td style={styles.td}>{row.menuo}</td>
            <td style={styles.td}>{row.diena}</td>
            <td style={styles.td}>{row.kodas}</td>
            <td style={styles.td}>{row['1']}</td>
            <td style={styles.td}></td>
            <td style={styles.td}></td>
            <td style={styles.td}></td>
            <td style={styles.td}>{row['2']}</td>
            <td style={styles.td}></td>
            <td style={styles.td}></td>
            <td style={styles.td}></td>
          </tr>          
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default HelpRequests;
