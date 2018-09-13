import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import CompanyList from '../../components/Company/CompanyList';
import * as companyActionCreators from '../../actions/company'
import './styles.css'

class CompanyListContainer extends Component {

  componentDidMount() {
    this.props.companyActions.fetchCompanyList();
  }

  render() {
    return (
      <div className="container">
        <p className="company-list-title">All stock items</p>
        <p className="company-list-subtitle">Choose the stock to start trading with</p>
        <div className="small-divider"/>
        <p className="company-list-sort-text">You can sort the table by clicking on the headers</p>
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
