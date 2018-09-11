import React, { Component } from 'react';
import './styles.css';
import TradeItemsContainer from './TradeItemsContainer';
import TradeItemsTable from './TradeItemsTable';
import Portfolio from './Portfolio';
import Transactions from './Transactions';

export default class Dashboard extends Component {
  render() {
    const { data, isFetching, error } = this.props.portfolio;
    if(error) {
      return <h1>{error.message}</h1>
    }
    if(isFetching) {
      return<h1>Loading...</h1>
    }
    return (
      <div className="pt-4">
        <div className="row">
          <div className="col-md-6">
            <div className="row">
              <div className="col-md-12">
                <h1 className="mt-5">Dashboard</h1>
                <p>Your overview of everything you have and want to know</p>
              </div>
              <div className="col-md-12">
                <Portfolio
                  title="Portfolio"
                  description="Here you see what you have and what is reserved."
                  moneyAvailable={data.amountOfMoney}
                  reserved={data.reservedAmountOfMoney}
                />
              </div>
            </div>
          </div>
          <div className="col-md-6">
            <TradeItemsContainer>
              <TradeItemsTable
                items={data.itemsInPossession}
                tableName="Trade items"
              />
              <TradeItemsTable
                items={data.itemsReserved}
                tableName="Trade items reserved"
              />
            </TradeItemsContainer>
          </div>
        </div>
        {/* <div className="row">
          <div className="col-md-12">
            <Transactions
              transactions={data.transactions}
              title="Transactions"
              description="Here you see what you have and what is reserved"
            />
          </div>
        </div> */}
      </div>
    )
  }
}
