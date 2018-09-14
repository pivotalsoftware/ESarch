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
        To use this tab please impersonate a user from <Link to="/">Home</Link>.
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
