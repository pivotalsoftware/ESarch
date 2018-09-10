import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { performLogin, performLogout } from '../../actions/auth';
import NavbarAuth from '../../components/Navbar/NavbarAuth';
import './styles.css';

class NavbarContainer extends Component {
  render() {
    const { isAuthenticated, impersonatedUser } = this.props;
    return (
      <nav className="navbar navbar-expand-lg navbar-dark bg-axon py-4">
        <div className="container">
          <Link className="navbar-brand" to="/">Axon Trader</Link>
          <ul className="navbar-nav mr-auto mt-2 mt-lg-0">
            <li className="nav-item">
              <Link className="nav-link" to="/">HOME</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/dashboard">DASHBOARD</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/companies">COMPANIES</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/portfolios">PORTFOLIO</Link>
            </li>
          </ul>
            {
              impersonatedUser ? 
                <span className="nav-item">
                {impersonatedUser.fullName} &nbsp;
                </span>
               : null
            }
            {
              impersonatedUser ? 
                <Link className="btn btn btn-outline-danger my-2 my-sm-0" to="/#credentials">Switch User</Link>
               : null
            }

        </div>  
      </nav>
    )
  }
}

function matchStateToProps(state) {
  return {
    isAuthenticated: state.auth.isAuthenticated,
    impersonatedUser: state.home.impersonatedUser
  };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ performLogin: performLogin, performLogout: performLogout }, dispatch);
}

export default connect(matchStateToProps, mapDispatchToProps)(NavbarContainer);
