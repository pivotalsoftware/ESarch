import React, { Component } from 'react';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import { addMoney, getPortfolioById, addItems } from '../actions/portfolio';
import Portfolio from '../components/Portfolios/Portfolio';

class PortfolioContainer extends Component {

  componentDidMount() {
    const id = this.props.match.params.id
    this.props.getPortfolioById(id);
  }

  render() {
    const { portfolio, addMoney, addItems } = this.props;
    return (
      <div className="container">
        <Portfolio
          portfolio={portfolio} 
          addMoney={addMoney}
          addItems={addItems}
        />
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    portfolio: state.portfolios.activePortfolio
  };
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ 
    addMoney: addMoney,
    getPortfolioById: getPortfolioById,
    addItems: addItems
  }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(PortfolioContainer);
