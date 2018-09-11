import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import BuyOrder from '../components/Company/BuyOrder';
import * as companyActionCreators from '../actions/company'

class BuyContainer extends Component {

  constructor(props) {
    super(props)

    this.onBack = this.goBack.bind(this)
    this.onPlaceOrder = this.placeOrder.bind(this)
  }

  componentDidUpdate(oldProps, oldState) {
    if(oldProps.buyOrder.isFetching && !this.props.buyOrder.isFetching && !this.props.buyOrder.error) {
      this.goBack()
    }
  }

  placeOrder(price, amount) {
    this.props.companyActions.placeBuyOrder(this.props.orderBook.identifier,
                                            this.props.portfolio.data.identifier,
                                            price,
                                            amount)
  }

  goBack() {
    this.props.history && this.props.history.goBack()
  }

  render() {
    return (
      <div className="container">
        <BuyOrder
          buyOrderHandler={this.onPlaceOrder}
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
    buyOrder: state.companies.buyOrder,
    portfolio: state.portfolio
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    companyActions: bindActionCreators(companyActionCreators, dispatch)
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(BuyContainer);
