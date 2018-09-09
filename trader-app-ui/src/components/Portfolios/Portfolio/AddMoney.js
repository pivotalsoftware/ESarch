import React, { Component } from 'react';

export default class AddMoney extends Component {

  constructor(props) {
    super(props);
    this.state = {
      amount: 0,
    }
  }

  handleChange = (event) => {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  handleSubmit = (event) => {
    event.preventDefault();
    const { amount } = this.state;
    this.props.addMoney(amount);
  }

  render() {
    const { amount } = this.state;
    return (
      <form
        name="form"
        onSubmit={this.handleSubmit}
        className="form-inline my-2 my-lg-0">
        <input
          type="number"
          min="0"
          className="form-control mr-sm-2"
          placeholder="0"
          name="amount"
          value={amount}
          onChange={this.handleChange}
        />
        <button
          type="submit"
          className="btn btn-outline-success my-2 my-sm-0">
          Add Money
        </button>
      </form>
    );

  }
}
