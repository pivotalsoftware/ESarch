import React from 'react';

const dataTable = (props) => {
  if(!props.data || props.data.length === 0) {
    return null;
  }

  return (
    <table className="table table-bordered company-table">
      <thead>
        <tr className="list-row-gray">
          <th className="company-list-cell-text company-list-cell-text-semibold">Count</th>
          <th className="company-list-cell-text company-list-cell-text-semibold">Price</th>
          {
            props.data[0].itemsRemaining !== undefined ? <th className="company-list-cell-text company-list-cell-text-semibold">Remaining</th> : null
          }
        </tr>
      </thead>
      <tbody>
        {
          props.data.map((item, index) => {
            const className = index % 2 == 0 ? "list-row-white" : "list-row-gray"
            const price = item.itemPrice ? item.itemPrice : item.tradePrice

            return (
              <tr className={className} key={`${index}-${item.tradeCount}-${price}`}>
                <td className="company-list-cell-text">{item.tradeCount}</td>
                <td className="company-list-cell-text">{price}</td>
                {
                  item.itemsRemaining ? <td className="company-list-cell-text">{item.itemsRemaining}</td> : null
                }
              </tr>
            )
          })
        }
      </tbody>
    </table>
  );
}

export default dataTable;
