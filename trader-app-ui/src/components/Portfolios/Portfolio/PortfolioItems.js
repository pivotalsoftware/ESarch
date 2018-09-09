import React from 'react';

const PortfolioItems = ({ items }) => 
  <div>
    <ul>
      {
        items.map(item => {
          return <li key={item.generatedId}>{item.companyName} : {item.amount}</li>
        })
      }
    </ul>
  </div>

  export default PortfolioItems;
