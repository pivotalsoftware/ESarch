import React from 'react';

const dataTable = (props) => {
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
            return (
              <tr key={`${i}-${item.count}-${item.price}`}>
                <td>{item.count}</td>
                <td>{item.price}</td>
                {
                  item.remaining ? <td>{item.remaining}</td> : null
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
