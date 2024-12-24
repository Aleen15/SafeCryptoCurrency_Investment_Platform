import React from "react";
import "./MainSection.css";
import land from "../Assets/land.jpg"

const MainSection = () => {
  return (
    <section className="main-section">
      <div className="content">
        <h1>Stock Market Investment Platform</h1>
        <p>
          A brand new platform to buy, sell your stocks through online....!
        </p>
        <div className="buttons">
          <a href="/login" target="_blank" rel="noopener noreferrer">
            <button className="btn-green">Login</button>
          </a>
          <a href="/register" target="_blank" rel="noopener noreferrer">
            <button className="btn-blue">Register</button>
          </a>
        </div>
      </div>
      <div className="mockups">
        <img
          src={land}
          alt="Device mockups"
          className="mockups-img"
        />
      </div>
    </section>
  );
};

export default MainSection;
