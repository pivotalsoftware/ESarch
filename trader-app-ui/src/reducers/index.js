import { combineReducers } from 'redux';
import companyReducer from './company';
import dashboardReducer from './dashboard';
import homeReducer from './home';

const rootReducer = combineReducers({
    home: homeReducer,
    companies: companyReducer,
    dashboard: dashboardReducer
});

export default rootReducer;
