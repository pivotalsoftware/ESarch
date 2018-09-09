import {
  FETCH_PORTFOLIO_REQUEST,
  FETCH_PORTFOLIO_SUCCESS,
  FETCH_PORTFOLIO_FAILURE,
  ADD_MONEY_SUCCESS,
  ADD_ITEMS_SUCCESS
} from '../constants/portfolioActions';
import { portfoliosMock } from '../mocks/portfolios';
import { status, json } from '../utils/fetch';

const API_ROOT = process.env.REACT_APP_API_ROOT;

export const addMoney = (value) => {
  console.log("Action called with value", value);
  return (dispatch, getState) => {
    const { portfolios } = getState();
    const newAmountOfMoney = portfolios.activePortfolio.data.amountOfMoney +
      parseInt(value);
    dispatch(addMoneySuccess(newAmountOfMoney));
  }
}

export const addItems = (companyName, amount) => {
  return (dispatch) => {
    dispatch(addItemsSuccess(companyName, amount))
  }
}

const addItemsSuccess = (companyName, amount) => {
  return {
    type: ADD_ITEMS_SUCCESS,


    // todo: fix add items action
    // selectedItem,
    // value,
    // allItems
  }
}

const addMoneySuccess = (amountOfMoney) => {
  return {
    type: ADD_MONEY_SUCCESS,
    payload: {
      amountOfMoney
    }
  }
}

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

export const getPortfolioById = (id) =>
  (dispatch) => {

    dispatch(fetchPortfolioRequest());

    // const options = {
    //   method: 'GET',
    //   headers: {
    //     'Content-Type': 'application/json',
    //   },
    // };

    // return fetch(`${API_ROOT}/query/portfolio/${id}`, options)
    // .then(status)
    // .then(json)
    // .then((data) => {
    //   // got a successfull response from the server
    //   dispatch(fetchPortfolioSuccess(data));
    // })
    // .catch((error) => {
    //   // bad response
    //   console.log(error);
    //   dispatch(fetchPortfolioFailure(error));
    // });


    setTimeout(
      () => dispatch(fetchPortfolioSuccess(portfoliosMock.items[id - 1])
      ), 500);

  }
