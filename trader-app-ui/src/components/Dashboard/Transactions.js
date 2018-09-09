import React from 'react';

const Transactions = ({ transactions, title, description }) => {
  return (
    <div>
      <h3>{title}</h3>
      <p>{description}</p>
      <table className="table table-bordered">
        <thead className="thead-light">
          <tr>
            <th>Name</th>
            <th>Type</th>
            <th># Items</th>
            <th>Price</th>
            <th>Executed</th>
            <th>State</th>
          </tr>
        </thead>
        <tbody>
          {
            transactions.map(transaction => {
              return (
                <tr key={transaction.company}>
                  <td>{transaction.company}</td>
                  <td>{transaction.type}</td>
                  <td>{transaction.itemsCount}</td>
                  <td>{transaction.price}</td>
                  <td>{transaction.executedCount}</td>
                  <td>{transaction.state}</td>
                </tr>
              );
            })
          }
        </tbody>
      </table>
    </div>
  );
}

export default Transactions;
