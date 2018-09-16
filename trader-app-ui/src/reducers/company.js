import {
  FETCH_COMPANY_LIST_REQUEST,
  FETCH_COMPANY_LIST_SUCCESS,
  FETCH_COMPANY_LIST_FAILURE,
  FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST,
  FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
  FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
  PLACE_BUY_ORDER_REQUEST,
  PLACE_BUY_ORDER_SUCCESS,
  PLACE_BUY_ORDER_FAILURE,
  PLACE_SELL_ORDER_REQUEST,
  PLACE_SELL_ORDER_SUCCESS,
  PLACE_SELL_ORDER_FAILURE,
  SET_ACTIVE_COMPANY
} from '../constants/companyActions';

const initialState = {
  companyList: {
    items: []
  },
  activeCompany: {
    index: null,
    orderBook: {
      isFetching: false,
      error: null,
      identifier: null,
      buy: [],
      sell: []
    }
  },
  buyOrder: {
    isFetching: false,
    error: null
  },
  sellOrder: {
    isFetching: false,
    error: null
  }
}

function companyReducer(state = initialState, action) {
  switch (action.type) {
    case FETCH_COMPANY_LIST_REQUEST:
      return Object.assign({}, state, {
        companyList: {
          isFetching: action.payload.isFetching,
          error: null
        }
      })
    case FETCH_COMPANY_LIST_SUCCESS:
      return Object.assign({}, state, {
        companyList: {
          isFetching: action.payload.isFetching,
          items: action.payload.data,
          error: null
        }
      })
    case FETCH_COMPANY_LIST_FAILURE:
      return Object.assign({}, state, {
        companyList: {
          isFetching: action.payload.isFetching,
          error: action.payload.error
        }
      })
    case FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST:
      return {
        ...state,
        activeCompany: {
          ...state.activeCompany,
          orderBook: {
            ...state.activeCompany.orderBook,
            isFetching: true
          }
        }
      }
    case FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS:
      const orderBook = action.payload.data && action.payload.data.length > 0 && action.payload.data[0]

      return {
        ...state,
        activeCompany: {
          ...state.activeCompany,
          orderBook: {
            isFetching: false,
            error: null,
            identifier: orderBook.identifier || null,
            buy: orderBook.buyOrders || [],
            sell: orderBook.sellOrders || []
          }
        }
      }
    case FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE:
      return {
        ...state,
        activeCompany: {
          ...state.activeCompany,
          orderBook: {
            isFetching: false,
            error: action.payload.error,
            identifier: null,
            buy: [],
            sell: []
          }
        }
      }
    case PLACE_BUY_ORDER_REQUEST:
      return {
        ...state,
        buyOrder: {
          isFetching: true,
          error: null
        }
      }
    case PLACE_BUY_ORDER_SUCCESS:
      return {
        ...state,
        buyOrder: {
          isFetching: false,
          error: null
        }
      }
    case PLACE_BUY_ORDER_FAILURE:
      return {
        ...state,
        buyOrder: {
          isFetching: false,
          error: action.payload.error
        }
      }
    case PLACE_SELL_ORDER_REQUEST:
      return {
        ...state,
        sellOrder: {
          isFetching: true,
          error: null
        }
      }
    case PLACE_SELL_ORDER_SUCCESS:
      return {
        ...state,
        sellOrder: {
          isFetching: false,
          error: null
        }
      }
    case PLACE_SELL_ORDER_FAILURE:
      return {
        ...state,
        sellOrder: {
          isFetching: false,
          error: action.payload.error
        }
      }
    case SET_ACTIVE_COMPANY:
      return {
        ...state,
        activeCompany: {
          ...state.activeCompany,
          index: action.payload.index
        }
      }
    default:
      return state
  }
}

export default companyReducer;

/*
The redux state for the company reducer (state.company) 

staeOfTheCompanyReducer = {

    companyList: {
        isFetching: boolean
        error: {
            message: string,
            statusCode: number
        }
        items:[
            {
                id:
                name:
                value:
                shares:
            },
            {
                id:
                name:
                value:
                shares:
            },
            {
                .....
            }
        ]
    },

    activeCompany: {
      id: 'sbajd
      orders: {
          isFetching: true,
          error: {}
        buy: [{
          count: "10",
          price: "200",
          remaining: "45"
        }],
        sell: []
      },
      executedTrades: {
        isFetching: true,
        error: {},
        trades: [{
          count: 33,
          price: 3333
        }]
      }
    }


}
*/