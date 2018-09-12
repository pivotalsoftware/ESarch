import {
  FETCH_PORTFOLIO_REQUEST,
  FETCH_PORTFOLIO_SUCCESS,
  FETCH_PORTFOLIO_FAILURE,
  FETCH_TRANSACTIONS_BY_PORTFOLIOID_REQUEST,
  FETCH_TRANSACTIONS_BY_PORTFOLIOID_SUCCESS,
  FETCH_TRANSACTIONS_BY_PORTFOLIOID_FAILURE
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

const fetchTransactionsByPortfolioIdRequest = () => (
  {
    type: FETCH_TRANSACTIONS_BY_PORTFOLIOID_REQUEST
  }
)

const fetchTransactionsByPortfolioIdSuccess = (data) => (
  {
    type: FETCH_TRANSACTIONS_BY_PORTFOLIOID_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)

const fetchTransactionsByPortfolioIdFailure = (error) => (
  {
    type: FETCH_TRANSACTIONS_BY_PORTFOLIOID_FAILURE,
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


export const getTransactionsByPortfolioId = (portfolioId) =>
  (dispatch) => {

    dispatch(fetchTransactionsByPortfolioIdRequest());

    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    };

    return fetch(`${API_ROOT}/query/transaction/by-portfolio/${portfolioId}`, options)
      .then(status)
      .then(json)
      .then((data) => {
        // got a successfull response from the server
        dispatch(fetchTransactionsByPortfolioIdSuccess(data));
      })
      .catch((error) => {
        // bad response
        console.log("Get transactions by portfolio id error", error);
        dispatch(fetchTransactionsByPortfolioIdFailure(error));
      });
  }


export const getPortfolioAndItsTransactions = (userId) => {
  return (dispatch, getState) => {
    return dispatch(getPortfolioByUserId(userId)).then(() => {
      const fetchedPortfolioId = getState().portfolio.data.identifier
      return dispatch(getTransactionsByPortfolioId(fetchedPortfolioId))
    })
  }
}
