import {
  FETCH_PORTFOLIO_REQUEST,
  FETCH_PORTFOLIO_SUCCESS,
  FETCH_PORTFOLIO_FAILURE,
} from '../constants/portfolioActions';
import { status, json } from '../utils/fetch';

const API_ROOT = process.env.REACT_APP_API_ROOT;

const fetchPortfolioRequest = () => (
  {
    type: FETCH_PORTFOLIO_REQUEST,
    payload: {
      isFetching: true
    }
  }
)

const fetchPortfolioSuccess = (data) => (
  {
    type: FETCH_PORTFOLIO_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)

const fetchPortfolioFailure = (error) => (
  {
    type: FETCH_PORTFOLIO_FAILURE,
    payload: {
      isFetching: false,
      error
    }
  }
)

export const getPortfolioByUserId = (userId) =>
  (dispatch) => {

    dispatch(fetchPortfolioRequest());

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
      dispatch(fetchPortfolioSuccess(data));
    })
    .catch((error) => {
      // bad response
      console.log("Get Portfolio By User Id Error", error);
      dispatch(fetchPortfolioFailure(error));
    });
  }
  