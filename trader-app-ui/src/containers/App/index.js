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
import rootReducer from '../../reducers';
import { scrollToAnimated } from '../../utils/animation'

const history = createHistory();
const middleware = routerMiddleware(history);

history.listen(location => {
  if(location.pathname === '/' && location.hash === '#credentials') {
    setTimeout(() => {
      scrollToAnimated(document.body.scrollHeight, 300)
    }, 50)
  } else {
    window.scrollTo(0, 0)
  }
})

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
              <SecureRoute exact path='/shares' component={Companies} />
              <SecureRoute exact path='/shares/:id' component={CompanyContainer} />
            </Switch>
          </div>
        </ConnectedRouter>
      </Provider>
    );
  }
}
