import {
  FETCH_PORTFOLIO_REQUEST,
  FETCH_PORTFOLIO_SUCCESS,
  FETCH_PORTFOLIO_FAILURE,
  FETCH_TRANSACTIONS_BY_PORTFOLIOID_REQUEST,
  FETCH_TRANSACTIONS_BY_PORTFOLIOID_SUCCESS,
  FETCH_TRANSACTIONS_BY_PORTFOLIOID_FAILURE,
} from '../constants/portfolioActions';

// expected format of the portfolio

// const portfolio = {
//   identifier: '392473kjshdfjkh',
//   userId: '34935kjehsfjkhsd',
//   amountOfMoney: 0,
//   reservedAmountOfMoney: 0,
//   itemsInPossession: {
//     '4798375kjhsdfjksh': {
//       generatedId: 23,
//       identifier: '347293kjdhfjks',
//       companyId: '35794skjdhfjkdshjkf',
//       companyName: 'Pivotal',
//       amount: 1000
//     }
//   itemsReserved: {},
//   }

const initialState = {
  isFetching: false,
  error: null,
  data: null,
  transactions: {
    isFetching: false,
    error: null,
    data: null,
  },
};

export default function portfolioReducer(state = initialState, action) {
  switch (action.type) {
    case FETCH_PORTFOLIO_REQUEST:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        error: null,
      };
    case FETCH_PORTFOLIO_SUCCESS:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        data: action.payload.data,
      };
    case FETCH_PORTFOLIO_FAILURE:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        error: action.payload.error,
      };
    case FETCH_TRANSACTIONS_BY_PORTFOLIOID_REQUEST:
      return {
        ...state,
        transactions: {
          isFetching: true,
          error: null,
          data: null,
        },
      };
    case FETCH_TRANSACTIONS_BY_PORTFOLIOID_SUCCESS:
      return {
        ...state,
        transactions: {
          isFetching: false,
          error: null,
          data: action.payload.data,
        },
      };
    case FETCH_TRANSACTIONS_BY_PORTFOLIOID_FAILURE:
      return {
        ...state,
        transactions: {
          isFetching: false,
          error: action.payload.error,
          data: null,
        },
      };
    default:
      return state;
  }
}
