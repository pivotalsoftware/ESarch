import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import Dashboard from '../components/Dashboard';
import * as portfolioActionCreators from '../actions/portfolio'

class DashboardContainer extends Component {

  componentDidMount() {
    this.props.portfolioActions.getPortfolioAndItsTransactions(this.props.impersonatedUser.userId);
  }

  render() {
    const { portfolio } = this.props;
    return (
      <div className="container">
        <Dashboard portfolio={portfolio} />
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    portfolioActions: bindActionCreators(portfolioActionCreators, dispatch)
  }
}

const mapStateToProps = state => {
  return {
    portfolio: state.portfolio,
    impersonatedUser: state.home.impersonatedUser
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(DashboardContainer);
