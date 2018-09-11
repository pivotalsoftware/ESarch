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
        <h1 className="mt-5">
          {company.name} <small>Value: {company.value} # Shares: {company.amountOfShares}</small>
        </h1>
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb">
            <li className="breadcrumb-item"><Link to="/">Home</Link></li>
            <li className="breadcrumb-item"><Link to="/companies">Companies</Link></li>
            <li className="breadcrumb-item active">{company.name}</li>
          </ol>
        </nav>
        <div className="row">
          <div className="col-md-2">
            <Link
              className="btn btn-primary btn-block"
              to={`/companies/${company.identifier}/buy`}>
                Buy
            </Link>
          </div>
          <div className="col-md-2">
            <button className="btn btn-primary btn-block" type="submit">Sell</button>
          </div>
        </div>
        <div className="row">
          <div className="col-md-4">
            <h2>Sell Orders</h2>
            <DataTable data={tradeDetails.orderBook.sell} />
          </div>
          <div className="col-md-4">
            <h2>Buy Orders</h2>
            <DataTable data={tradeDetails.orderBook.buy} />
          </div>
          <div className="col-md-4">
            <h2>Executed Trades</h2>
            <DataTable data={tradeDetails.executedTrades.trades} />
          </div>
        </div>

      </div>
    );
  }
}

export default Company;