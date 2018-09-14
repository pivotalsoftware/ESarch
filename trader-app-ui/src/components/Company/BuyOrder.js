import React, { Component } from 'react';
import './styles.css'

export default class BuyOrder extends Component {

  constructor(props) {
    super(props)

    this.formSubmitHandler = this.onFormSubmit.bind(this);
    this.priceChangeHandler = this.onPriceChanged.bind(this);
    this.amountChangeHandler = this.onAmountChanged.bind(this);

    this.state = {
      priceToTrade: 0,
      amountToTrade: 0
    }
  }

  onFormSubmit(event) {
    event.preventDefault();

    this.props.buyOrderHandler && this.props.buyOrderHandler(this.state.priceToTrade, this.state.amountToTrade)
  }

  onPriceChanged(event) {
    this.setState({
      priceToTrade: event.target.value
    })
  }

  onAmountChanged(event) {
    this.setState({
      amountToTrade: event.target.value
    })
  }

  render() {
    const { company, portfolio, cancelHandler } = this.props;

    return (
      <div>
        <h1 className="mt-5">
          Buy order for: {company.name}
        </h1>
        {portfolio.data && <div className="mt-3 mb-5 buyOrderMoneyAvailableContainer">
          <p>{portfolio.data.amountOfMoney} cents available of which {portfolio.data.reservedAmountOfMoney} cents reserved</p>
        </div>}
        <h4 className="mb-1">Enter items to buy and for how much</h4>
        <form onSubmit={this.formSubmitHandler}>
          <div className="form-group">
            <label>Price to trade:</label>
            <input type="number" placeholder="0" onChange={this.priceChangeHandler}/>
          </div>
          <div className="form-group">
            <label>Amount of items to trade:</label>
            <input type="number" placeholder="0" onChange={this.amountChangeHandler}/>
          </div>
          <div style={{flexDirection: 'row'}}>
            <button type="submit">PLACE ORDER</button>
            <button type="reset">RESET</button>
            <button type="button" onClick={cancelHandler}>CANCEL</button>
          </div>
        </form>
      </div>
    );
  }
}
