import React from 'react';

const dataTable = (props) => {
  if(!props.data || props.data.length === 0) {
    return null;
  }

  return (
    <table className="table">
      <thead>
        <tr>
          <th>Count</th>
          <th>Price</th>
          {
            Object.keys(props.data[0]).length === 3 ? <th>Remaining</th> : null
          }
        </tr>
      </thead>
      <tbody>
        {
          props.data.map((item, i) => {
            const price = item.itemPrice ? item.itemPrice : item.tradePrice

            return (
              <tr key={`${i}-${item.tradeCount}-${price}`}>
                <td>{item.tradeCount}</td>
                <td>{price}</td>
                {
                  item.itemsRemaining ? <td>{item.itemsRemaining}</td> : null
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
