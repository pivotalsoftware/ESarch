import {
  FETCH_PORTFOLIO_LIST_REQUEST,
  FETCH_PORTFOLIO_LIST_SUCCESS,
  FETCH_PORTFOLIO_LIST_FAILURE
} from '../constants/portfolioActions';
import { portfoliosMock } from '../mocks/portfolios';

const fetchPortfolioListRequest = () => (
  {
    type: FETCH_PORTFOLIO_LIST_REQUEST,
    payload: {
      isFetching: true
    }
  }
)

const fetchPortfolioListSuccess = (data) => (
  {
    type: FETCH_PORTFOLIO_LIST_SUCCESS,
    payload: { 
      portfolios: data, 
      isFetching: false
    }
  }
)

const fetchPortfolioListFailure = (error) => (
  {
    type: FETCH_PORTFOLIO_LIST_FAILURE,
    payload: {
      error,
      isFetching: false
    }
  }
)

export const fetchPortfolioList = () => 
  (dispatch) => {
    dispatch(fetchPortfolioListRequest());

    // GET /portfolios

    setTimeout(
      () => dispatch(fetchPortfolioListSuccess(portfoliosMock)
      ), 500);
  }
