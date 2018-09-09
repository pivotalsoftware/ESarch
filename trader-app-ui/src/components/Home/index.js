import React, { Component } from 'react';
import CredentialsTable from './CredentialsTable';
import credentials from './credentials';
import './styles.css';
import SideBar from './SideBar';
import Banner from './Banner';
import Header from './Header';

class Home extends Component {

  render() {
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
              <CredentialsTable credentials={credentials} />
            </div>
            <div className="col-md-6 text-right pt-5">
              <SideBar />
            </div>
          </div>
        </div>
      </div>
    )
  }
}

export default Home;