import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import DataTable from './DataTable';

class Company extends Component {

  render() {

    const { isFetching, error, data } = this.props.company;

    if (error) {
      return <h1 className="mt-5">Error loading page! {error.message}</h1>
    }

    if (isFetching) {
      return <h1 className="mt-5">Loading...</h1>
    }

    if (!this.props.company) {
      return null;
    }

    return (
      <div>
        <h1 className="mt-5">
          {data.name} <small>Value: {data.value} # Shares: {data.shares}</small>
        </h1>
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb">
            <li className="breadcrumb-item"><Link to="/">Home</Link></li>
            <li className="breadcrumb-item"><Link to="/companies">Companies</Link></li>
            <li className="breadcrumb-item active">{data.name}</li>
          </ol>
        </nav>
        <div className="row">
          <div className="col-md-2">
            <button className="btn btn-primary btn-block" type="submit">Buy</button>
          </div>
          <div className="col-md-2">
            <button className="btn btn-primary btn-block" type="submit">Sell</button>
          </div>
        </div>
        <div className="row">
          <div className="col-md-4">
            <h2>Sell Orders</h2>
            <DataTable data={data.sellOrders} />
          </div>
          <div className="col-md-4">
            <h2>Buy Orders</h2>
            <DataTable data={data.buyOrders} />
          </div>
          <div className="col-md-4">
            <h2>Executed Trades</h2>
            <DataTable data={data.executedTrades} />
          </div>
        </div>

      </div>
    );
  }
}

export default Company;