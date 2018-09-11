import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import BuyOrder from '../components/Company/BuyOrder';
import * as companyActionCreators from '../actions/company'

class BuyContainer extends Component {
  render() {
    return (
      <div className="container">
        <BuyOrder
          buyOrderHandler={this.props.companyActions.buyOrder}
          company={this.props.company} />
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    company: state.companies.companyList.items[
      state.companies.activeCompany.index
    ]
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    companyActions: bindActionCreators(companyActionCreators, dispatch)
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(BuyContainer);
