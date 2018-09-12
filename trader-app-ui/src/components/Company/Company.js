import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import DataTable from './DataTable';

class Company extends Component {

  render() {
    const { company, tradeDetails } = this.props;

    if (!company) {
      return null;
    }

    return (
      <div>

        <div className="row h-75 align-items-center mb-2">
          <div className="col-sm-8">
            <span className="company-details-title">{company.name}</span>
          </div>
          <div className="col-sm-2">
            <Link
              className="btn btn-primary btn-block axon-button"
              to={`/companies/${company.identifier}/sell`}>
                SELL
            </Link>
          </div>
          <div className="col-sm-2">
            <Link
              className="btn btn-primary btn-block axon-button"
              to={`/companies/${company.identifier}/buy`}>
                BUY
            </Link>
          </div>
        </div>

        <div className="row mb-5">
          <div className="col-12">
            <div className="company-details-share-info-container">
              <p className="company-details-share-info-text">VALUE  {company.value.toLocaleString('en')}  /  SHARES  {company.amountOfShares.toLocaleString('en')}</p>
            </div>
          </div>
        </div>

        <div className="row">
          <div className="col-md-4">
            <h5 className="company-orders-tables-header mb-3">Sell Orders</h5>
            <DataTable data={tradeDetails.orderBook.sell} />
          </div>
          <div className="col-md-4">
            <h5 className="company-orders-tables-header mb-3">Buy Orders</h5>
            <DataTable data={tradeDetails.orderBook.buy} />
          </div>
          <div className="col-md-4">
            <h5 className="company-orders-tables-header mb-3">Executed Trades</h5>
            <DataTable data={tradeDetails.executedTrades.trades} />
          </div>
        </div>

      </div>
    );
  }
}

export default Company;