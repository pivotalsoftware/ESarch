import {
  FETCH_PORTFOLIO_LIST_REQUEST,
  FETCH_PORTFOLIO_LIST_SUCCESS,
  FETCH_PORTFOLIO_LIST_FAILURE,
  FETCH_PORTFOLIO_REQUEST,
  FETCH_PORTFOLIO_SUCCESS,
  FETCH_PORTFOLIO_FAILURE,
  ADD_MONEY_SUCCESS,
  ADD_MONEY_FAILURE,
  ADD_ITEMS_SUCCESS,
  ADD_ITEMS_FAILURE
} from '../constants/portfolioActions';

const initialState = {
  portfolios: {
    items: [],
    error: null,
    isFetching: false
  },
  activePortfolio: {
    data: {
      itemsInPossession: [],
      itemsReserved: []
    },
    error: null,
    isFetching: false
  }
}

export default function (state = initialState, action) {
  switch (action.type) {
    case FETCH_PORTFOLIO_LIST_REQUEST:
      return {
        ...state,
        portfolios: {
          isFetching: action.payload.isFetching,
          error: null
        }
      }
    case FETCH_PORTFOLIO_LIST_SUCCESS:
      return {
        ...state,
        portfolios: {
          items: action.payload.portfolios.items,
          isFetching: action.payload.isFetching
        }
      }
    case FETCH_PORTFOLIO_LIST_FAILURE:
      return {
        ...state,
        portfolios: {
          error: action.payload.error,
          isFetching: action.payload.isFetching
        }
      }
    case FETCH_PORTFOLIO_REQUEST:
      return {
        ...state,
        activePortfolio: {
          isFetching: action.payload.isFetching,
          error: null
        }
      }
    case FETCH_PORTFOLIO_SUCCESS:
      return {
        ...state,
        activePortfolio: {
          isFetching: action.payload.isFetching,
          data: action.payload.data
        }
      }
    case FETCH_PORTFOLIO_FAILURE:
      return {
        ...state,
        activePortfolio: {
          isFetching: action.payload.isFetching,
          error: action.payload.error
        }
      }
    case ADD_MONEY_SUCCESS: {
      return {
        ...state,
        activePortfolio : {
          data: {
            ...state.activePortfolio.data,
            amountOfMoney: action.payload.amountOfMoney
          }
        }
      }
    }
    case ADD_ITEMS_SUCCESS: {
      const currentActivePortfolioData = state.activePortfolio.data;

      action.allItems.map((item, i) => {
        if (item.name === action.selectedItem) {
          action.allItems[i].count = action.allItems[i].count + action.value;
        }
      });
    
      return {
        ...state,
        activePortfolio: {
          data: {
            ...currentActivePortfolioData,
            itemsAvailable: action.allItems
          }
        }
      }
    }
    default:
      return state;
  }
}