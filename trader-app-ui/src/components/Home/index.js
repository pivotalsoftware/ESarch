import React, { Component } from 'react';
import CredentialsTable from './CredentialsTable';
import './styles.css';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import SideBar from './SideBar';
import Banner from './Banner';
import Header from './Header';
import * as homeActionCreators from '../../actions/home'

class Home extends Component {

  componentDidMount() {
    this.props.homeActions.getUsers();
  }

  render() {
    const { users } = this.props;

    return (
      <div className="axon-homepage">
        <div className="container">
          <div className="row">
            <div className="offset-md-6 col-md-6 text-right">
              <Banner />
            </div>
          </div>
          <div className="row">
            <div className="col-md-6">
              <Header />
              <CredentialsTable id="credentials" credentials={users} onSetImpersonatedUser={this.setImpersonatedUser}/>
            </div>
            <div className="col-md-6 text-right pt-5">
              <SideBar />
            </div>
          </div>
        </div>
      </div>
    )
  }

  setImpersonatedUser = (user) => {
      this.props.homeActions.setImpersonatedUser(user);
      this.props.history.push('/dashboard');
  }
}

const mapDispatchToProps = (dispatch) => {
    return {
      homeActions: bindActionCreators(homeActionCreators, dispatch)
    }
  }
  
  const mapStateToProps = state => {
    return {
      users: state.home.users
    }
  }
  
  export default connect(mapStateToProps, mapDispatchToProps)(Home);