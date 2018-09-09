import React from 'react';

const TradeItemsTable = ({ items, tableName }) => {
  return (
    <div>
      <h4 className="mt-5 text-white">{tableName}</h4>
      <table className="table table-bordered">
        <thead className="thead-light">
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
                  <td> {items[key].amount}</td>
                </tr>
            ))
        }
        </tbody>
      </table>
    </div>
  );
}

export default TradeItemsTable;
