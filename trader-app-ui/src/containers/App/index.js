import React, { Component } from 'react';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import createHistory from "history/createBrowserHistory";
import { ConnectedRouter, routerMiddleware, } from "react-router-redux";
import { Route, Switch } from 'react-router-dom';
import NavbarContainer from '../NavbarContainer';
import LoginContainer from '../LoginContainer';
import Home from '../../components/Home';
import DashboardContainer from '../DashboardContainer';
import Companies from '../CompanyListContainer/CompanyListContainer';
import SecureRoute from '../SecureRoute';
import CompanyContainer from '../CompanyContainer';
import BuyContainer from '../BuyContainer';
import SellContainer from '../SellContainer';
import rootReducer from '../../reducers';

const history = createHistory();
const middleware = routerMiddleware(history);

const store = createStore(
  rootReducer,
  window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__(),
  applyMiddleware(thunk, middleware)
);

export default class App extends Component {
  render() {
    return (
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <div>
            <NavbarContainer />
            <Switch>
              <Route exact path='/' component={Home} />
              <Route exact path='/login' component={LoginContainer} />
              <SecureRoute exact path="/dashboard" component={DashboardContainer} />
              <SecureRoute exact path='/companies' component={Companies} />
              <SecureRoute exact path='/companies/:id' component={CompanyContainer} />
              <SecureRoute exact path='/companies/:id/buy' component={BuyContainer} />
              <SecureRoute exact path='/companies/:id/sell' component={SellContainer} />
            </Switch>
          </div>
        </ConnectedRouter >
      </Provider>
    );
  }
}
