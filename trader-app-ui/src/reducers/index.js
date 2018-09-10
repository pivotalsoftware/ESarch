import { combineReducers } from 'redux';
import authReducer from './auth';
import portfoliosReducer from './portfolio';
import companyReducer from './company';
import dashboardReducer from './dashboard';
import homeReducer from './home';

const rootReducer = combineReducers({
    home: homeReducer,
    portfolios: portfoliosReducer,
    auth: authReducer,
    companies: companyReducer,
    dashboard: dashboardReducer
});

export default rootReducer;
