import React, { Component } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { connect } from 'react-redux';
import './styles.css';

class NavbarContainer extends Component {

  render() {
    const { impersonatedUser } = this.props;

    return (
      <nav className="navbar navbar-expand-lg navbar-dark bg-axon py-4">
        <div className="container">
          <Link className="navbar-brand" to="/">Axon Trader</Link>
          <ul className="navbar-nav mr-auto mt-2 mt-lg-0">
            <li className="nav-item">
              <NavLink
                exact={true}
                className='nav-link'
                activeClassName='nav-link active'
                to="/">HOME</NavLink>
            </li>
            <li className="nav-item">
              <NavLink
                className='nav-link'
                activeClassName='nav-link active'
                to="/dashboard">DASHBOARD</NavLink>
            </li>
            <li className="nav-item">
              <NavLink
                className='nav-link'
                activeClassName='nav-link active'
                to="/shares">SHARES</NavLink>
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
    impersonatedUser: state.home.impersonatedUser,
    location: state.router.location
  };
}

export default connect(matchStateToProps)(NavbarContainer);
