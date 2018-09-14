import React, { Component } from 'react';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware, compose } from 'redux';
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
import rootReducer from '../../reducers';
import { loadState, saveState } from './localStorage';

const history = createHistory();
const middleware = routerMiddleware(history);
const persistedState = loadState();

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(
  rootReducer,
  persistedState,
  composeEnhancers(applyMiddleware(thunk, middleware))
);

store.subscribe(() => {
  saveState(store.getState());
})

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
            </Switch>
          </div>
        </ConnectedRouter >
      </Provider>
    );
  }
}
