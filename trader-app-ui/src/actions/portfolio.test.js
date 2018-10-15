import {
  fetchPortfolioRequest,
  fetchPortfolioSuccess,
  fetchPortfolioFailure,
} from './portfolio';
import * as actions from '../constants/portfolioActions';

describe('portfolioActions: fetch portfolio request', () => {
  it('should create an action to request a portfolio', () => {
    const expectedAction = {
      type: actions.FETCH_PORTFOLIO_REQUEST,
      payload: {
        isFetching: true,
      },
    };
    expect(fetchPortfolioRequest()).toEqual(expectedAction);
  });
});

describe('portfolioActions: fetch portfolio success', () => {
  it('should create an action to handle when a portfolio is fetched successfully', () => {
    const response = [
      {
        identifier: 'safh43fh1234',
        userId: '456456757dfgd43',
        amountOfMoney: '2000',
      },
    ];
    const expectedAction = {
      type: actions.FETCH_PORTFOLIO_SUCCESS,
      payload: {
        isFetching: false,
        data: response,
      },
    };
    expect(fetchPortfolioSuccess(response)).toEqual(expectedAction);
  });
});

describe('portfolioActions: fetch portfolio failure', () => {
  it('should create an action to handle errors when fetching a portfolio', () => {
    const error = {
      message: 'Not Found',
      status: 404,
    };
    const expectedAction = {
      type: actions.FETCH_PORTFOLIO_FAILURE,
      payload: {
        isFetching: false,
        error,
      },
    };
    expect(fetchPortfolioFailure(error)).toEqual(expectedAction);
  });
});
