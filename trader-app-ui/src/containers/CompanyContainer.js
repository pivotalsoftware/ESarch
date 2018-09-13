import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import Company from '../components/Company/Company';
import * as companyActionCreators from '../actions/company'

class CompanyContainer extends Component {

  componentDidMount() {
    // get the id for the company from the url
    const id = this.props.match.params.id;
    this.props.companyActions.setActiveCompany(id);
    this.props.companyActions.fetchOrderBooksByCompanyId(id);
  }

  componentDidUpdate(oldProps, oldState) {
    if(oldProps.tradeDetails.orderBook.isFetching && !this.props.tradeDetails.orderBook.isFetching
        && !this.props.tradeDetails.orderBook.error) {
          const orderBookId = this.props.tradeDetails.orderBook.identifier
          this.props.companyActions.fetchExecutedTradesByOrderBookId(orderBookId)
        }
  }

  render() {
    const { company, tradeDetails } = this.props;
    return (
      <div className="container mt-5">
        <Company company={company} tradeDetails={tradeDetails} />
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
    company: state.companies.companyList.items[
      state.companies.activeCompany.index
    ],
    tradeDetails: state.companies.activeCompany
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(CompanyContainer);
