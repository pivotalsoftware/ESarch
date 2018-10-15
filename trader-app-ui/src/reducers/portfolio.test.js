import portfolioReducer from './portfolio';
import * as actions from '../constants/portfolioActions';

describe('portfolio reducer', () => {
  it('should return the initial state', () => {
    expect(portfolioReducer(undefined, {})).toEqual(
      {
        isFetching: false,
        error: null,
        data: null,
        transactions: {
          isFetching: false,
          error: null,
          data: null,
        },
      },
    );
  });

  it('should handle FETCH_PORTFOLIO_REQUEST', () => {
    expect(portfolioReducer({}, {
      type: actions.FETCH_PORTFOLIO_REQUEST,
      payload: {
        isFetching: true,
      },
    })).toEqual(
      {
        isFetching: true,
        error: null,
      },
    );
  });

  it('should handle FETCH_PORTFOLIO_SUCCESS', () => {
    expect(portfolioReducer({}, {
      type: actions.FETCH_PORTFOLIO_SUCCESS,
      payload: {
        isFetching: false,
        data: {
          identifier: '2387sjh',
          amountOfMoney: 10000,
        },
      },
    })).toEqual(
      {
        isFetching: false,
        data: {
          identifier: '2387sjh',
          amountOfMoney: 10000,
        },
      },
    );
  });

  it('should handle FETCH_PORTFOLIO_FAILURE', () => {
    expect(portfolioReducer({}, {
      type: actions.FETCH_PORTFOLIO_FAILURE,
      payload: {
        isFetching: false,
        error: {
          message: 'Not Found',
        },
      },
    })).toEqual(
      {
        isFetching: false,
        error: {
          message: 'Not Found',
        },
      },
    );
  });

  it('should handle FETCH_TRANSACTIONS_BY_PORTFOLIOID_REQUEST', () => {
    expect(portfolioReducer({}, {
      type: actions.FETCH_TRANSACTIONS_BY_PORTFOLIOID_REQUEST,
    })).toEqual(
      {
        transactions: {
          isFetching: true,
          error: null,
          data: null,
        },
      },
    );
  });

  it('should handle FETCH_TRANSACTIONS_BY_PORTFOLIOID_SUCCESS', () => {
    expect(portfolioReducer({}, {
      type: actions.FETCH_TRANSACTIONS_BY_PORTFOLIOID_SUCCESS,
      payload: {
        data: [
          { identifier: '4987sdjhjds', companyName: 'Solstice' },
          { identifier: '3259843hdfhds', companyName: 'Pivotal' },
        ],
      },
    })).toEqual(
      {
        transactions: {
          isFetching: false,
          error: null,
          data: [
            { identifier: '4987sdjhjds', companyName: 'Solstice' },
            { identifier: '3259843hdfhds', companyName: 'Pivotal' },
          ],
        },
      },
    );
  });

  it('should handle FETCH_TRANSACTIONS_BY_PORTFOLIOID_FAILURE', () => {
    expect(portfolioReducer({}, {
      type: actions.FETCH_TRANSACTIONS_BY_PORTFOLIOID_FAILURE,
      payload: {
        error: {
          message: 'Not Found',
        },
      },
    })).toEqual(
      {
        transactions: {
          isFetching: false,
          error: {
            message: 'Not Found',
          },
          data: null,
        },
      },
    );
  });
});
