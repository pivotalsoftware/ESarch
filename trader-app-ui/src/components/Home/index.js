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
          <div className="row align-items-center">
            <div className="col-sm-6 px-0">
              <img src={require('../../assets/home-foreground-image.png')}
                   style={{width: '100%', height: '100%'}}/>
            </div>
            <div className="col-sm-6 mt-2 pt-5 text-right">
              <Banner />
            </div>
          </div>
          <div className="row mt-5 align-items-end">
            <div className="col-md-6">
              <Header />
            </div>
            <div className="col-md-6 text-left pl-5 pb-5 pr-0">
              <SideBar />
            </div>
          </div>
          <div className="row pb-5">
            <div className="col-md-6">
              <CredentialsTable id="credentials" credentials={users} onSetImpersonatedUser={this.setImpersonatedUser}/>
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