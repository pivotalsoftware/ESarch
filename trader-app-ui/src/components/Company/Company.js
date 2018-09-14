import React, { Component } from 'react';
import DataTable from './DataTable';
import SellOrder from './SellOrder';
import BuyOrder from './BuyOrder';

class Company extends Component {
  constructor(props) {
    super(props);

    this.state = {
      buyModalOPen: false,
      sellModalOpen: false
    }

    this.openBuyModal = this.openBuyModal.bind(this);
    this.openSellModal = this.openSellModal.bind(this);
    this.closeModal = this.closeModal.bind(this);
  }

  componentDidUpdate(oldProps, oldState) {
    if((oldProps.buyOrder.isFetching && !this.props.buyOrder.isFetching && !this.props.buyOrder.error)||
    (oldProps.sellOrder.isFetching && !this.props.sellOrder.isFetching && !this.props.sellOrder.error)) {
      // close modal after successfully making a request
      this.closeModal();
    }
  }

  openBuyModal() {
    this.setState({
      buyModalOpen: true
    })
  }
  openSellModal() {
    this.setState({
      sellModalOpen: true
    })
  }

  closeModal() {
    this.setState({
      buyModalOpen: false,
      sellModalOpen: false
    })
  }

  render() {
    const { company, tradeDetails, portfolio, sellOrderHandler, buyOrderHandler } = this.props;
    const { sellModalOpen, buyModalOpen } = this.state;

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
            <button
              className="btn btn-primary btn-block axon-button"
              onClick={this.openSellModal}>
              SELL
            </button>
          </div>
          <div className="col-sm-2">
            <button
              className="btn btn-primary btn-block axon-button"
              onClick={this.openBuyModal}>
              BUY
            </button>
          </div>
        </div>

        {
          sellModalOpen &&
          <SellOrder
            sellOrderHandler={sellOrderHandler}
            cancelHandler={this.closeModal}
            company={company}
            portfolio={portfolio}
          />
        }
        {buyModalOpen &&
          <BuyOrder
            buyOrderHandler={buyOrderHandler}
            cancelHandler={this.closeModal}
            company={company}
            portfolio={portfolio}
          />
        }

        <div className="row mb-5">
          <div className="col-12">
            <div className="company-details-share-info-container">
              <p className="company-details-share-info-text">
                VALUE {(company.value / 100).toLocaleString('en', { style: 'currency', currency: 'USD' })} /  SHARES {company.amountOfShares.toLocaleString('en')}
              </p>
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