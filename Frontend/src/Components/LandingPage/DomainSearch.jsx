import React from "react";
import "./DomainSearch.css";

const DomainSearch = () => {
  return (
    <section className="domain-search">
      <h2>Search Your Domain Name</h2>
      <p>A small river named Duden flows by their place</p>
      <div className="search-box">
        <input type="text" placeholder="Enter your domain name..." />
        <select>
          <option value=".com">.com</option>
          <option value=".net">.net</option>
          <option value=".biz">.biz</option>
          <option value=".co">.co</option>
          <option value=".me">.me</option>
        </select>
        <button>Search</button>
      </div>
      {/* <div className="pricing">
        <span>.com $9.75</span>
        <span>.net $9.50</span>
        <span>.biz $8.95</span>
        <span>.co $7.80</span>
        <span>.me $7.95</span>
      </div> */}
    </section>
  );
};

export default DomainSearch;
