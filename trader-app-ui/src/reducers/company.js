import {
  FETCH_COMPANY_LIST_REQUEST,
  FETCH_COMPANY_LIST_SUCCESS,
  FETCH_COMPANY_LIST_FAILURE,
  FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST,
  FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
  FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
  SET_ACTIVE_COMPANY
} from '../constants/companyActions';

const initialState = {
  companyList: {
    items: []
  },
  activeCompany: {},
  companyOrderBook: {
      isFetching: true,
      data: {},
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
      return Object.assign({}, state, {
        companyOrderBook: {
          isFetching: action.payload.isFetching,
          error: null
        }
      })
    case FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS:
      return Object.assign({}, state, {
        companyOrderBook: {
          isFetching: action.payload.isFetching,
          data: action.payload.data,
          error: null
        }
      })
    case FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE:
      return Object.assign({}, state, {
        companyOrderBook: {
          isFetching: action.payload.isFetching,
          error: action.payload.error
        }
      })
    case SET_ACTIVE_COMPANY:
      return Object.assign({}, state, {
        activeCompany: action.payload.activeCompany
      })
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
      isFetching: boolean
      error: {
          message: string,
          statusCode: number
      }
      data: {
        id: id,
        name: "Bp",
        value: "15000",
        shares: "100000",
        sellOrders: [
          {
            count: "10",
            price: "200",
            remaining: "45"
          }
        ],
        buyOrders: [
          {
            count: "20",
            price: "199",
            remaining: "20"
          }
        ],
        executedTrades: [
          {
            count: "20",
            price: "198"
          }
        ]
      }
    }


}
*/