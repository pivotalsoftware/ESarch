import React from 'react';

const TradeItemsTable = ({ items, tableName }) => {
  return (
    <div>
      <h4 className="mt-4 trade-items-title">{tableName}</h4>
      <table className="table table-bordered dashboard-trades-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Amount</th>
          </tr>
        </thead>
        <tbody>
        {
            Object.keys(items).map((key, index) => (
                <tr key={key}>
                  <td> {items[key].companyName}</td>
                  <td> {items[key].amount.toLocaleString('en')}</td>
                </tr>
            ))
        }
        </tbody>
      </table>
    </div>
  );
}

export default TradeItemsTable;
