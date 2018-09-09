import React, { Component } from 'react';

export default class AddItems extends Component {

  constructor(props) {
    super(props);
    this.state = {
      companyName: 'select',
      amount: 0
    }
  }

  handleChange = (event) => {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  handleSubmit = (event) => {
    event.preventDefault();
    const { companyName, amount } = this.state;
    this.props.addItems(companyName, amount);
  }

  render() {
    const { companyName, amount } = this.state;
    const { portfolio } = this.props;
    return (
      <form
        name="form"
        onSubmit={this.handleSubmit}
        className="form-inline my-2 my-lg-0">

        <select
          name="companyName"
          onChange={this.handleChange}
          value={companyName}
          className="form-control mr-sm-2">
          <option value="select">Select</option>
          {
            portfolio.data.itemsInPossession.map(item =>
              <option
                value={item.companyName}
                key={item.generatedId}>
                {item.companyName}
              </option>)
          }
        </select>
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
          disabled={companyName === 'select'}
          type="submit"
          className="btn btn-outline-success my-2 my-sm-0">
          Add Items
        </button>
      </form>
    );

  }
}
