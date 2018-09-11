import { combineReducers } from 'redux';
import companyReducer from './company';
import homeReducer from './home';
import portfolioReducer from './portfolio';

const rootReducer = combineReducers({
    home: homeReducer,
    companies: companyReducer,
    portfolio: portfolioReducer
});

export default rootReducer;
