
import React, { Component } from 'react';
import PortfolioListContainer from '../../../containers/PortfolioListContainer';

class Portfolios extends Component {
    render() {
        return (
            <div className="container">
                <h1 className="mt-5">All portfolios</h1>
                <span>Choose the portfolio to watch the details for</span>
                <PortfolioListContainer />
            </div>
        )
    }
}

export default Portfolios;
