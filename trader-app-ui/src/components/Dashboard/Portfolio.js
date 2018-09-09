import React from 'react';

const Portfolio = (props) => {
  return (
    <div>
      <h4>{props.title}</h4>
      <p>{props.description}</p>
      <table className="table table-bordered">
        <thead className="thead-light">
          <tr>
            <th>Money</th>
            <th>&nbsp;</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Available</td>
            <td>{props.moneyAvailable}</td>
          </tr>
          <tr>
            <td>Reserved</td>
            <td>{props.reserved}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default Portfolio;
