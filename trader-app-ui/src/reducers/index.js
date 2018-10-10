import { combineReducers } from 'redux';
import { routerReducer } from 'react-router-redux';
import companyReducer from './company';
import homeReducer from './home';
import portfolioReducer from './portfolio';

const rootReducer = combineReducers({
  router: routerReducer,
  home: homeReducer,
  companies: companyReducer,
  portfolio: portfolioReducer,
});

export default rootReducer;
