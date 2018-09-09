
import React, { Component } from 'react';
import styles from './portfolio_detail.scss';
import PortfolioItems from './PortfolioItems';
import AddMoney from './AddMoney';
import AddItems from './AddItems';

export default class Prtfolio extends Component {

  constructor(props) {
    super(props);
    this.state = {
      amount: 0,
      itemsCount: 0,
      selectedItem: 'select',
    }
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  render() {
    const { amount, itemsCount, selectedItem } = this.state;

    if(this.props.portfolio.error) {
      return <h1>Error: {this.props.portfolio.error.message}</h1>;
    }

    if (this.props.portfolio.isFetching) {
      return <h1>Loading...</h1>;
    }

    return (
      <div className="container">
        <h3 className="mt-5">
          Profile detail : {this.props.portfolio.data.userName}
        </h3>
        <div>Here you can add money and items to the portfolio.</div>
        <div className="col-sm-6">
          <div className={styles.content}>
            <h3>Money</h3>
            <div>
              <div className="pull-left">Available</div>
              <div className={styles.amount}>{this.props.portfolio.data.amountOfMoney}</div>
            </div><br />
            <div>
              <div className="pull-left">Reserved</div>
              <div className={styles.amount}>{this.props.portfolio.data.reservedAmountOfMoney}</div>
            </div><br />
            <h3>Items In possession</h3>
            <PortfolioItems items={this.props.portfolio.data.itemsInPossession} />
            <h3>Reserved</h3>
            <PortfolioItems items={this.props.portfolio.data.itemsReserved} />
          </div>
        </div>
        <div className="col-sm-6">
          <div className={styles.content}>


            <AddMoney addMoney={this.props.addMoney} />
          </div>
          <div className={styles.content}>
            <AddItems portfolio={this.props.portfolio} addItems={this.props.addItems} />
          </div>
        </div>
      </div>
    )
  }
}
