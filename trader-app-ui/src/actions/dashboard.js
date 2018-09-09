import {
  FETCH_DASHBOARD_REQUEST,
  FETCH_DASHBOARD_SUCCESS,
  FETCH_DASHBOARD_FAILURE,
} from '../constants/dashboardActions';
import { status, json } from '../utils/fetch';

const API_ROOT = process.env.REACT_APP_API_ROOT;

const fetchDashboardRequest = () => (
  {
    type: FETCH_DASHBOARD_REQUEST,
    payload: {
      isFetching: true
    }
  }
)

const fetchDashboardSuccess = (data) => (
  {
    type: FETCH_DASHBOARD_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)

const fetchDashboardFailure = (error) => (
  {
    type: FETCH_DASHBOARD_FAILURE,
    payload: {
      isFetching: false,
      error
    }
  }
)

export const getPortfolioByUserId = (userId) =>
  (dispatch) => {

    dispatch(fetchDashboardRequest);

    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    };

    return fetch(`${API_ROOT}/query/portfolio/by-user/${userId}`, options)
    .then(status)
    .then(json)
    .then((data) => {
      // got a successfull response from the server
      dispatch(fetchDashboardSuccess(data));
    })
    .catch((error) => {
      // bad response
      console.log("Get Portfolio By User Id Error", error);
      dispatch(fetchDashboardFailure(error));
    });
  }