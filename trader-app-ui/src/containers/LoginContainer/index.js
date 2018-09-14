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
      <div className="container text-center">
        <h2 className="login-title mt-5">To use this tab please impersonate a user from <Link to="/">Home</Link></h2>
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
