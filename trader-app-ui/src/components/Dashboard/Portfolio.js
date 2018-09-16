import React from 'react';

const Portfolio = (props) => {
  const moneyFormatOptions = { style: 'currency', currency: 'USD' }

  return (
    <div>
      <h4 className="portfolio-title">{props.title}</h4>
      <div className="dashboard-divider"/>
      <p className="portfolio-subtitle">{props.description}</p>
      <table className="table table-bordered dashboard-table">
        <thead className="thead-light">
          <tr>
            <th>Money</th>
            <th>&nbsp;</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Available</td>
            <td>{(props.moneyAvailable).toLocaleString('en', moneyFormatOptions)}</td>
          </tr>
          <tr>
            <td>Reserved</td>
            <td>{(props.reserved).toLocaleString('en', moneyFormatOptions)}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default Portfolio;
