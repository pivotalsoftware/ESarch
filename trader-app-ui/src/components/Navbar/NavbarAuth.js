import React, { Component } from 'react';
import './styles.css';

export default class NavbarAuth extends Component {

  constructor(props) {
    super(props)
    this.state = {
      username: '',
      password: '',
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  handleSubmit(event) {
    event.preventDefault();
    const { username, password } = this.state;
    this.props.performLogin(username, password);
  }

  render() {
    const { isAuthenticated, performLogout, user } = this.props;
    const { username, password } = this.state;
    if (isAuthenticated) {
      return (
        <div>
          <span className="nav-item text-light">
            {user.fullName} &nbsp;
          </span>
          <button
            onClick={performLogout}
            className="btn btn btn-outline-danger my-2 my-sm-0">
            Logout
          </button>
        </div>
      )
    }

    return (
      <form
        name="form"
        onSubmit={this.handleSubmit}
        className="form-inline my-2 my-lg-0">
        <input
          type="text"
          className="form-control mr-sm-2 navbar-input"
          placeholder="Username"
          name="username"
          value={username}
          onChange={this.handleChange}
        />
        <input
          type="password"
          className="form-control mr-sm-2 navbar-input"
          name="password"
          placeholder="Password"
          value={password}
          onChange={this.handleChange}
        />
        <button
          type="submit"
          value="login"
          className="btn btn-outline-light my-2 my-sm-0">
          Sign in
        </button>
      </form>
    )
  }
}
