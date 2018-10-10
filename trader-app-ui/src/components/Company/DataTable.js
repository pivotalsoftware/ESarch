import React from 'react';

const dataTable = props => (
  <div className="table-responsive">
    <table className="table table-bordered company-table">
      <thead>
        <tr className="list-row-gray">
          <th className="company-list-cell-text company-list-cell-text-semibold">Count</th>
          <th className="company-list-cell-text company-list-cell-text-semibold">Price</th>
        </tr>
      </thead>
      <tbody>
        {
            props.data && props.data.map((item, index) => {
              const className = index % 2 === 0 ? 'list-row-white' : 'list-row-gray';
              const price = item.itemPrice ? item.itemPrice : item.tradePrice;
              
              return (
                <tr className={className} key={`${index}-${item.itemsRemaining}-${price}`}>
                  <td className="company-list-cell-text">{item.itemsRemaining.toLocaleString('en')}</td>
                  <td className="company-list-cell-text">{price.toLocaleString('en', { style: 'currency', currency: 'USD' })}</td>
                </tr>
              );
            })
          }
      </tbody>
    </table>
  </div>
);

export default dataTable;
