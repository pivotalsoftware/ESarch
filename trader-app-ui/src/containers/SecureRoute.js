import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Route, Redirect } from 'react-router-dom';

const SecureRoute = ({ component: ComposedComponent, ...rest }) => {
  class Authentication extends Component {
    // redirect if not authenticated; otherwise, return the component passed into <SecureRoute />
    handleRender(props) {
      if (!this.props.impersonatedUser) {
        return <Redirect to="/login" />;
      }
      return <ComposedComponent {...props} />;
    }

    render() {
      return (
        <Route {...rest} render={this.handleRender.bind(this)} />
      );
    }
  }

  function mapStateToProps(state) {
    return {
      impersonatedUser: state.home.impersonatedUser,
    };
  }

  const AuthenticationContainer = connect(mapStateToProps)(Authentication);
  return <AuthenticationContainer />;
};

export default SecureRoute;
