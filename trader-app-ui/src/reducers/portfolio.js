import {
  FETCH_PORTFOLIO_REQUEST,
  FETCH_PORTFOLIO_SUCCESS,
  FETCH_PORTFOLIO_FAILURE,
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
//   transactions: {}
//   }

const initialState = {
    isFetching: false,
    error: null,
    data: null
  }

export default function portfolioReducer(state = initialState, action) {
    switch (action.type) {
    case FETCH_PORTFOLIO_REQUEST:
  return {
    ...state,
    isFetching: action.payload.isFetching,
    error: null
  }
    case FETCH_PORTFOLIO_SUCCESS:
  return {
    ...state,
    isFetching: action.payload.isFetching,
    data: action.payload.data
  }
    case FETCH_PORTFOLIO_FAILURE:
  return {
    ...state,
    isFetching: action.payload.isFetching,
    error: action.payload.error
  }
    default:
  return state
}
}
