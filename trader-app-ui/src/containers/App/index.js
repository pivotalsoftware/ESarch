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
import Companies from '../CompanyListContainer';
import PrivateRoute from '../PrivateRoute';
import CompanyContainer from '../CompanyContainer';
import BuyContainer from '../BuyContainer';
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
              <PrivateRoute exact path="/dashboard" component={DashboardContainer} />
              <PrivateRoute exact path='/companies' component={Companies} />
              <PrivateRoute exact path='/companies/:id' component={CompanyContainer} />
              <PrivateRoute exact path='/companies/:id/buy' component={BuyContainer} />
            </Switch>
          </div>
        </ConnectedRouter >
      </Provider>
    );
  }
}
