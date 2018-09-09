import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import Company from '../components/Company/Company';
import * as companyActionCreators from '../actions/company'

class CompanyContainer extends Component {

  componentDidMount() {
    // get the id for the company from the url
    const id = this.props.match.params.id;
    this.props.companyActions.fetchCompany(id);
  }

  render() {
    const { companies } = this.props;
    return (
      <div className="container">
        <Company company={companies.activeCompany} />
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

export default connect(mapStateToProps, mapDispatchToProps)(CompanyContainer);
