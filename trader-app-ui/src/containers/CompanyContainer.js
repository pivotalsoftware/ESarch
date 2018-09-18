import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import Company from '../components/Company/Company';
import * as companyActionCreators from '../actions/company'

const API_ROOT = process.env.REACT_APP_API_ROOT;

class CompanyContainer extends Component {
  constructor(props) {
    super(props);

    this.state = {
        eventSource: null
    }

    this.sellOrderHandler = this.sellOrderHandler.bind(this);
    this.buyOrderHandler = this.buyOrderHandler.bind(this);
  }

  componentDidMount() {
    // get the id for the company from the url
    const id = this.props.match.params.id;

    this.props.companyActions.fetchCompanyById(id);
    this.props.companyActions.fetchOrderBooksByCompanyId(id);
  }

  componentWillUnmount() {
      if(this.state.eventSource) {
        this.state.eventSource.close();
      }
  }

  componentDidUpdate(oldProps, oldState) {
    if(oldProps.activeCompany.orderBook.isFetching && !this.props.activeCompany.orderBook.isFetching
        && !this.props.activeCompany.orderBook.error) {
        const orderBookId = this.props.activeCompany.orderBook.identifier;

        // let eventSource = new EventSource(`${API_ROOT}/query/subscribe/order-book/${orderBookId}`);
        // eventSource.onmessage = (event) => {
        //   if(event.data) {
        //     let jsonData = JSON.parse(event.data);
        //     console.log('event json data', jsonData);
        //     this.props.companyActions.setSSEOrderBookData(jsonData);
        //   }
        // }

        // this.setState({
        //   eventSource: eventSource
        // })
    }

    if((oldProps.buyOrder.isFetching && !this.props.buyOrder.isFetching && !this.props.buyOrder.error)||
      (oldProps.sellOrder.isFetching && !this.props.sellOrder.isFetching && !this.props.sellOrder.error)) {
      // refetch order book after successfully posting a transaction
      this.props.companyActions.fetchOrderBooksByCompanyId(this.props.match.params.id);
    }
  }

  sellOrderHandler(price, amount) {
    this.props.companyActions.placeSellOrder(
      this.props.orderBook.identifier,
      this.props.portfolio.data.identifier,
      price,
      amount
    )
  }

  buyOrderHandler(price, amount) {
    this.props.companyActions.placeBuyOrder(
      this.props.orderBook.identifier,
      this.props.portfolio.data.identifier,
      price,
      amount
    )
  }

  render() {
    const {
      activeCompany,
      portfolio,
      sellOrder,
      buyOrder,
    } = this.props;

    return (
      <div className="container pt-5">
        <Company
          activeCompany={activeCompany}
          portfolio={portfolio}
          sellOrderHandler={this.sellOrderHandler}
          buyOrderHandler={this.buyOrderHandler}
          sellOrder={sellOrder}
          buyOrder={buyOrder}
          />
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    companyActions: bindActionCreators(companyActionCreators, dispatch)
  }
}

const mapStateToProps = state => {
  return {
    activeCompany: state.companies.activeCompany,
    portfolio: state.portfolio,
    orderBook: state.companies.activeCompany.orderBook,
    sellOrder: state.companies.sellOrder,
    buyOrder: state.companies.buyOrder
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(CompanyContainer);
