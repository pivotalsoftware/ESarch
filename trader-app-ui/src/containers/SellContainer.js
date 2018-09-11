import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import SellOrder from '../components/Company/SellOrder';
import * as companyActionCreators from '../actions/company'

class SellContainer extends Component {

  constructor(props) {
    super(props)

    this.onBack = this.goBack.bind(this)
    this.onPlaceOrder = this.placeOrder.bind(this)
  }

  componentDidUpdate(oldProps, oldState) {
    if(oldProps.sellOrder.isFetching && !this.props.sellOrder.isFetching && !this.props.sellOrder.error) {
      this.goBack()
    }
  }

  placeOrder(price, amount) {
    this.props.companyActions.placeSellOrder(this.props.orderBook.identifier,
                                            this.props.portfolio.data.identifier,
                                            price,
                                            amount);
  }

  goBack() {
    this.props.history && this.props.history.goBack()
  }

  render() {
    return (
      <div className="container">
        <SellOrder
          sellOrderHandler={this.onPlaceOrder}
          cancelHandler={this.onBack}
          company={this.props.company}
          portfolio={this.props.portfolio} />
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    company: state.companies.companyList.items[
      state.companies.activeCompany.index
    ],
    orderBook: state.companies.activeCompany.orderBook,
    sellOrder: state.companies.sellOrder,
    portfolio: state.portfolio
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    companyActions: bindActionCreators(companyActionCreators, dispatch)
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(SellContainer);
