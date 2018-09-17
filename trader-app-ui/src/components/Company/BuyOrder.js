import React, { Component } from 'react';
import './styles.css'

export default class BuyOrder extends Component {

  constructor(props) {
    super(props)

    this.formResetHandler = this.onFormReset.bind(this);
    this.formSubmitHandler = this.onFormSubmit.bind(this);
    this.priceChangeHandler = this.onPriceChanged.bind(this);
    this.amountChangeHandler = this.onAmountChanged.bind(this);

    this.state = {
      priceToTrade: null,
      amountToTrade: null
    }
  }

  onFormReset(event) {
    event.preventDefault();

    this.setState({
      priceToTrade: null,
      amountToTrade: null
    })
  }

  onFormSubmit(event) {
    event.preventDefault();

    this.props.buyOrderHandler && this.props.buyOrderHandler(this.state.priceToTrade, this.state.amountToTrade)
  }

  onPriceChanged(event) {
    this.setState({
      priceToTrade: parseInt(event.target.value.replace(',', ''), 10)
    })
  }

  onAmountChanged(event) {
    this.setState({
      amountToTrade: parseInt(event.target.value.replace(',', ''), 10)
    })
  }

  render() {
    const { company, portfolio, cancelHandler } = this.props;
    const currencyStyle = { style: 'currency', currency: 'USD' }
    const price = this.state.priceToTrade ? this.state.priceToTrade.toLocaleString('en') : ''
    const amount = this.state.amountToTrade ? this.state.amountToTrade.toLocaleString('en') : ''

    return (
      <div className="modal fade show modal-transaction">
        <div className="modal-dialog modal-lg modal-dialog-centered">
          <div className="modal-content">
            <form onSubmit={this.formSubmitHandler}>
              <div className="modal-header">
                <h1 className="modal-title company-transaction-header mt-5">
                  Buy order for: <span className="company-name">{company.name}</span>
                </h1>
                <button type="button" onClick={cancelHandler} className="close" aria-label="Close">
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
              <div className="modal-body">
                {portfolio.data && <div className="money-available-container mb-4">
                  <p className="money-available-text">
                    {portfolio.data.amountOfMoney.toLocaleString('en', currencyStyle)} dollars available of which {portfolio.data.reservedAmountOfMoney.toLocaleString('en', currencyStyle)} dollars reserved
                  </p>
                </div>}

                <h4 className="company-transaction-title my-3">Enter items to buy and for how much</h4>
                <div className="form-group row">
                  <label className="col-sm-6 col-form-label transaction-form-lable">Price to trade:</label>
                  <div className="col-sm-6">
                    <input className="form-control transaction-form-control" value={price} type="text" min="0" placeholder="0" onChange={this.priceChangeHandler} />
                  </div>
                </div>
                <div className="form-group row">
                  <label className="col-sm-6 col-form-label transaction-form-lable">Amount of items to trade:</label>
                  <div className="col-sm-6">
                    <input className="form-control transaction-form-control" value={amount} type="text" min="0" placeholder="0" onChange={this.amountChangeHandler} />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <div>
                  <button className="btn btn-transaction-primary" type="submit">PLACE ORDER</button>
                  <button className="btn btn-transaction-default" type="reset" onClick={this.formResetHandler}>RESET</button>
                  <button className="btn btn-transaction-default" type="button" onClick={cancelHandler}>CANCEL</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    );
  }
}
