import React, { Component } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { connect } from 'react-redux';
import './styles.css';

class NavbarContainer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showItems: false
    }
    this.toggleNavbar = this.toggleNavbar.bind(this);
  }
  
  toggleNavbar() {
    this.setState({
      showItems: !this.state.showItems
    })
  }

  render() {
    const { impersonatedUser } = this.props;
    const { showItems } = this.state;

    const classToggler = showItems ? 'navbar-toggler' : 'navbar-toggler collapsed';
    const classCollapse = showItems ? 'collapse navbar-collapse show' : 'collapse navbar-collapse';

    return (
      <nav className="navbar navbar-expand-lg navbar-dark bg-axon py-4">
        <div className="container">
          <Link className="navbar-brand" to="/">Axon Trader</Link>
          <button className={classToggler} type="button" onClick={this.toggleNavbar}>
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className={classCollapse}>
            <ul className="navbar-nav navbar-item-list mr-auto mt-2 mt-lg-0">
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
                  to="/orderbooks">ORDER BOOKS</NavLink>
              </li>
            </ul>
            {
              impersonatedUser &&
              <p className="current-username">
                {impersonatedUser.fullName} &nbsp;
                </p>
            }
            {
              impersonatedUser ?
                <Link className="btn btn-light axon-button my-2 my-sm-0" to="/#credentials">Switch User</Link>
                : null
            }
          </div>
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
