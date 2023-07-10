import React, { useState, useEffect } from "react";
import "./date.css";
import "./img.css";

const TimeDate = () => {
  const [currTime, setTime] = useState(new Date().toLocaleTimeString());
  const [since, setSince] = useState(0);
  const [firstTime, setFirstTime] = useState(new Date().getTime());
  const [isPause, setPause] = useState(true);
  const [Img, setImg] = useState("");
  useEffect(() => {
    const interval = setInterval(() => {
      const time = new Date().toLocaleTimeString();
      setTime(time);
    }, 1000);

    const updateSince = () => {
      const currentTime = new Date().getTime();
      const timeDifference = currentTime - firstTime;
      const secondsSince = Math.floor(timeDifference / 1000);
      if (!isPause) setSince("Time to relax");
      else {
        if (secondsSince < 60)
          setSince(`Time since last break: ${secondsSince} seconds`);
        else if (secondsSince / 60 < 60)
          setSince(
            `Time since last break: ${Math.floor(secondsSince / 60)} minutes`
          );
        else
          setSince(
            `Time since last break: ${Math.floor(secondsSince / 3600)} hours`
          );
      }

      if (secondsSince < 60 * 20) setImg("kebab1.png");
      else if (secondsSince < 60 * 40) setImg("kebab2.png");
      else if (secondsSince < 60 * 60) setImg("kebab3.png");
      else if (secondsSince < 60 * 80) setImg("kebab4.png");
      else if (secondsSince < 60 * 100) setImg("kebab6.png");
      else setImg("kebab5.png");
    };
    updateSince();
    const sinceInterval = setInterval(updateSince, 1000);

    return () => {
      clearInterval(interval);
      clearInterval(sinceInterval);
    };
  }, [firstTime, isPause]);

  const resetFirstTime = () => {
    if (isPause) {
      setPause(false);
    } else {
      setFirstTime(new Date().getTime());
      setPause(true);
    }
  };

  return (
    <div className="date" style={{ position: "relative", bottom: "0px" }}>
      <h1 style={{ position: "fixed", bottom: "0px", right: "10px" }}>
        {currTime},{since}
      </h1>
      <div className="img" style={{ position: "relative", bottom: "45px", left: "20px" }}>
        <img src={Img} alt="test" width="100" height="175" />
      </div>
      <button
        onClick={resetFirstTime}
        style={{ position: "relative", bottom: "10px", left: "45px" }}
      >
        {isPause ? "Pause" : "Return to work"}
      </button>
    </div>
  );
};

export default TimeDate;
