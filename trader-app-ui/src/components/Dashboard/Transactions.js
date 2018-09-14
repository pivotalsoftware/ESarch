import React from 'react';

const Transactions = ({ transactions, title, description }) => {
  return (
    <div>
      <h3 className="portfolio-title mt-4">{title}</h3>
      <div className="dashboard-divider"/>
      <p className="portfolio-subtitle">{description}</p>
      <table className="table table-bordered dashboard-table">
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
                <tr key={transaction.identifier}>
                  <td>{transaction.companyName}</td>
                  <td>{transaction.type}</td>
                  <td>{transaction.amountOfItems.toLocaleString('en')}</td>
                  <td>{(transaction.pricePerItem / 100).toLocaleString('en', { style: 'currency', currency: 'USD' })}</td>
                  <td>{transaction.amountOfExecutedItems.toLocaleString('en')}</td>
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
