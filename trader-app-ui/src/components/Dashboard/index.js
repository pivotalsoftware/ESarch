import React, { Component } from 'react';
import './styles.css';
import TradeItemsContainer from './TradeItemsContainer';
import TradeItemsTable from './TradeItemsTable';
import Portfolio from './Portfolio';
import Transactions from './Transactions';
import Loader from '../Loader';

export default class Dashboard extends Component {
  constructor(props) {
    super(props);

    this.renderHeader = this.renderHeader.bind(this);
  }

  renderHeader() {
    return (
      <div>
        <h1 className="mt-5 dashboard-header">Dashboard</h1>
        <p className="mb-5 dashboard-subheader">
          Your overview of everything you have and want to know
        </p>
      </div>
    );
  }

  render() {
    const {
      data, isFetching, error, transactions,
    } = this.props.portfolio;

    if (error) {
      return (
        <div className="pt-4">
          {this.renderHeader()}
          <h1 className="axon-error">{error.message}</h1>
        </div>
      );
    }

    if (isFetching) {
      return (
        <div className="pt-4">
          <div className="row">
            <div className="col-sm-6 pr-5">
              {this.renderHeader()}
            </div>
          </div>
          <Loader className="centered-loader" />
        </div>
      );
    }

    return (
      <div className="pt-4">
        <div className="row">
          <div className="col-md-6 pr-5">
            <div className="row">
              <div className="col-md-12">
                {this.renderHeader()}
              </div>
            </div>
            <div className="row">
              <div className="col-md-12">
                {
                  data
                  && (
                  <Portfolio
                    title="Portfolio"
                    description="Here you see what you have and what is reserved"
                    moneyAvailable={data.amountOfMoney}
                    reserved={data.reservedAmountOfMoney}
                  />
                  )
                }
              </div>
            </div>
          </div>
          <div className="col-md-6 align-self-end mb-3">
            {
              data
              && (
              <TradeItemsContainer>
                <TradeItemsTable
                  items={data.itemsInPossession}
                  tableName="Trade Shares"
                />
                <TradeItemsTable
                  items={data.itemsReserved}
                  tableName="Trade Shares reserved"
                />
              </TradeItemsContainer>
              )
            }
          </div>
        </div>
        <div className="row mb-5">
          <div className="col-md-12">
            {
              transactions.data
              && (
              <Transactions
                transactions={transactions.data}
                title="Transactions"
                description="Here you see your current transactions"
              />
              )
            }
          </div>
        </div>
      </div>
    );
  }
}
