import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import CompanyList from '../components/Company/CompanyList';
import * as companyActionCreators from '../actions/company'

class CompanyListContainer extends Component {

  componentDidMount() {
    this.props.companyActions.fetchCompanyList();
  }

  render() {
    return (
      <div className="container">
        <h1 className="mt-5">
          All stock items<small> Choose the stock to start trading with</small>
        </h1>
        <div>
          <CompanyList companies={this.props.companies.companyList} />
        </div>
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
    companies: state.companies
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(CompanyListContainer);
