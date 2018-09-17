import React, { Component } from 'react';
import './styles.css';
import TradeItemsContainer from './TradeItemsContainer';
import TradeItemsTable from './TradeItemsTable';
import Portfolio from './Portfolio';
import Transactions from './Transactions';

export default class Dashboard extends Component {
  render() {
    const { data, isFetching, error, transactions } = this.props.portfolio;
    if(error) {
      return <h1>{error.message}</h1>
    }
    if(isFetching) {
      return<h1>Loading...</h1>
    }
    return (
      <div className="pt-4">
        <div className="row">
          <div className="col-md-6 pr-5">
            <div className="row">
              <div className="col-md-12">
                <h1 className="mt-5 dashboard-header">Dashboard</h1>
                <p className="mb-5 dashboard-subheader">
                  Your overview of everything you have and want to know
                </p>
              </div>
              <div className="col-md-12">
                {
                  data &&
                  <Portfolio
                    title="Portfolio"
                    description="Here you see what you have and what is reserved."
                    moneyAvailable={data.amountOfMoney}
                    reserved={data.reservedAmountOfMoney}
                  />
                }
              </div>
            </div>
          </div>
          <div className="col-md-6 align-self-end mb-3">
            {
              data &&
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
            }
          </div>
        </div>
        <div className="row">
          <div className="col-md-12">
            {
              transactions.data &&
              <Transactions
                transactions={transactions.data}
                title="Transactions"
                description="Here you see what you have and what is reserved"
              />
            }
          </div>
        </div>
      </div>
    )
  }
}
