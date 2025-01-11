import { Doughnut } from "react-chartjs-2";
import PropTypes from "prop-types";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import styles from "./HalfDoughnutChart.module.scss";

ChartJS.register(ArcElement, Tooltip, Legend);

const HalfDoughnutChart = ({ productivity }) => {
  const productivityPercent = (productivity * 100).toFixed(1);

  const getColor = (value) => {
    const red = value < 0.5 ? 255 : Math.floor(255 - (value - 0.5) * 2 * 255);
    const green = value > 0.5 ? 255 : Math.floor(value * 2 * 255);
    return `rgba(${red}, ${green}, 0, 1)`;
  };

  const data = {
    datasets: [
      {
        data: [productivity, 1 - productivity],
        backgroundColor: [getColor(productivity), "rgba(200, 200, 200, 0.5)"],
        borderWidth: 0.5,
        cutout: "50%",
        rotation: -90,
        circumference: 180,
      },
    ],
  };

  const options = {
    plugins: {
      tooltip: { enabled: false },
    },
  };

  return (
    <div className={styles.chartContainer}>
      <Doughnut data={data} options={options} />
      <div className={styles.centeredText}>{productivityPercent}%</div>
    </div>
  );
};

HalfDoughnutChart.propTypes = {
  productivity: PropTypes.number.isRequired,
};

export default HalfDoughnutChart;
