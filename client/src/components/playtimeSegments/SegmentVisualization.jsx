import PropTypes from "prop-types";
import { useState } from "react";
import styles from "./SegmentVisualization.module.scss";

const SegmentVisualization = ({ segmentData }) => {
  const [hoverData, setHoverData] = useState(null);

  if (!segmentData || segmentData.length === 0) {
    return (
      <div className={styles.noDataMessage}>
        This employee has no playtime data
      </div>
    );
  }

  const totalMinutes = 1440;
  const startMinute = 361;

  const scaledSegmentData = new Array(totalMinutes).fill(0);

  segmentData.forEach(({ timeSegment, count }) => {
    const adjustedSegment =
      (timeSegment + totalMinutes - startMinute) % totalMinutes;
    scaledSegmentData[adjustedSegment] = count;
  });

  const minValue = Math.min(
    ...scaledSegmentData.filter((v) => v > 0),
    Infinity
  );
  const maxValue = Math.max(...scaledSegmentData);

  const getColor = (value) => {
    if (value === 0) return "transparent";
    const brightness = 100 - ((value - minValue) / (maxValue - minValue)) * 100;
    return `hsl(0, 100%, ${brightness}%)`;
  };

  const handleMouseMove = (e, index) => {
    const barRect = e.currentTarget.getBoundingClientRect();
    const timeInMinutes = (startMinute + index) % totalMinutes;

    const hour = Math.floor(timeInMinutes / 60);
    const minutes = timeInMinutes % 60;
    const formattedTime = `${hour.toString().padStart(2, "0")}:${minutes
      .toString()
      .padStart(2, "0")}`;

    setHoverData({
      time: formattedTime,
      count: scaledSegmentData[index],
      position: {
        x: e.clientX - barRect.left,
        y: e.clientY - barRect.top,
      },
    });
  };

  const handleMouseLeave = () => setHoverData(null);

  return (
    <div className={styles.visualizationContainer}>
      <div className={styles.timeBarContainer}>
        <div className={styles.timeBar}>
          {scaledSegmentData.map((value, index) => (
            <div
              key={index}
              className={styles.segment}
              style={{ backgroundColor: getColor(value) }}
              onMouseMove={(e) => handleMouseMove(e, index)}
              onMouseLeave={handleMouseLeave}
            />
          ))}
        </div>
        {hoverData && (
          <div
            className={styles.hoverInfo}
            style={{
              left: `${hoverData.position.x}px`,
              top: `${hoverData.position.y - 20}px`,
            }}
          >
            <div>Time: {hoverData.time}</div>
            <div>Count: {hoverData.count}</div>
          </div>
        )}
      </div>

      <div className={styles.axisLabels}>
        {scaledSegmentData.map((_, index) => {
          if (index % (totalMinutes / 24) === 0) {
            const timeInMinutes = (startMinute + index) % totalMinutes;
            const hour = Math.floor(timeInMinutes / 60);
            const formattedHour = hour.toString().padStart(2, "0");
            return (
              <span
                key={index}
                className={styles.label}
                style={{
                  left: `${(index / scaledSegmentData.length) * 100}%`,
                }}
              >
                {formattedHour}:00
              </span>
            );
          }
          return null;
        })}
      </div>
    </div>
  );
};

SegmentVisualization.propTypes = {
  segmentData: PropTypes.arrayOf(
    PropTypes.shape({
      timeSegment: PropTypes.number.isRequired,
      count: PropTypes.number.isRequired,
    })
  ).isRequired,
};

export default SegmentVisualization;
