import React from 'react';

const TradeItemsContainer = (props) => {
  let className = 'container-trade-items';

  if (props.className) {
    className += ` ${props.className}`;
  }

  return (
    <div className={className}>
      {props.children}
    </div>
  );
};

export default TradeItemsContainer;
