import React, { Component } from 'react';
import { performLogin } from '../../actions/auth';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Redirect } from 'react-router-dom'
import './styles.css';

class LoginContainer extends Component {

  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password: '',
      remember: false
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;

    this.setState({
      [name]: value 
    });
  }

  handleSubmit(event) {
    event.preventDefault();
    const { username, password } = this.state;
    this.props.performLogin(username, password);
  }

  render() {
    const { username, password, remember } = this.state;
    const { isAuthenticated } = this.props;

    if(isAuthenticated) {
      return <Redirect to="/dashboard"/>
    }

    return (
      <div className="container text-center">
        <form
          name="form"
          onSubmit={this.handleSubmit}
          className="form-signin">
          <p>
            You need to login to access this part of the site. Please provide
            your username and password Login to get access
          </p>
          <h1 className="h3 mb-3 font-weight-normal">
            Login to get access
          </h1>
          <input
            type="text"
            className="form-control"
            name="username"
            value={username}
            onChange={this.handleChange}
          />
          <input
            type="password"
            className="form-control"
            name="password"
            value={password}
            onChange={this.handleChange}
          />
          <div className="checkbox mb-3">
            <label>
              <input
                type="checkbox"
                name="remember"
                checked={remember}
                onChange={this.handleChange}
              />
              &nbsp; Don't ask for my password for two weeks
          </label>
          </div>
          <button className="btn btn-primary">Login</button>
        </form>
      </div>
    )
  }
}

const mapDispatchToProps = (dispatch) => {
  return bindActionCreators({ performLogin: performLogin }, dispatch);
}

function matchStateToProps(state) {
  return {
    isAuthenticated: state.auth.isAuthenticated,
  };
}

export default connect(matchStateToProps, mapDispatchToProps)(LoginContainer);
