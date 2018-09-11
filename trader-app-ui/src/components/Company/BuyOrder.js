import React, { Component } from 'react';

export default class BuyOrder extends Component {
  render() {
    const { company, buyOrderHandler } = this.props;
    return (
      <div>
        <h1 className="mt-5">
          Buy order for: {company.name}
        </h1>
      </div>
    );
  }
}
