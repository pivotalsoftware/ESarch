import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect, Link } from 'react-router-dom'
import './styles.css';

class LoginContainer extends Component {

  render() {
    const { impersonatedUser } = this.props;

    if(impersonatedUser) {
      return <Redirect to="/dashboard"/>
    }

    return (
      <div className="container auth-error-container">
        <span className="align-middle auth-error-message">
          Please <Link className="auth-error-message link" to="/#credentials">impersonate a user</Link> to access this page
        </span>
      </div>
    )
  }
}

function matchStateToProps(state) {
  return {
    impersonatedUser: state.home.impersonatedUser,
  };
}

export default connect(matchStateToProps)(LoginContainer);
