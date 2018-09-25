import React from 'react';
import { formatTransactionState } from '../../utils/transactions'

const Transactions = ({ transactions, title, description }) => {
  return (
    <div>
      <h3 className="portfolio-title mt-4">{title}</h3>
      <div className="dashboard-divider" />
      <p className="portfolio-subtitle">{description}</p>
      <div className="table-responsive">
        <table className="table table-bordered dashboard-table">
          <thead className="thead-light">
            <tr>
              <th>Name</th>
              <th>Type</th>
              <th># Shares</th>
              <th>Price</th>
              <th>Total Price</th>
              <th>Shares Executed</th>
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
                    <td>{(transaction.pricePerItem).toLocaleString('en', { style: 'currency', currency: 'USD' })}</td>
                    <td>{(transaction.amountOfItems * transaction.pricePerItem).toLocaleString('en', { style: 'currency', currency: 'USD' })}</td>
                    <td>{transaction.amountOfExecutedItems.toLocaleString('en')}</td>
                    <td>{formatTransactionState(transaction.state)}</td>
                  </tr>
                );
              })
            }
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Transactions;
