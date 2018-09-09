import {
  FETCH_COMPANY_LIST_REQUEST,
  FETCH_COMPANY_LIST_SUCCESS,
  FETCH_COMPANY_LIST_FAILURE,
  FETCH_COMPANY_REQUEST,
  FETCH_COMPANY_SUCCESS,
  FETCH_COMPANY_FAILURE
} from '../constants/companyActions';

const initialState = {
  companyList: {
    items: []
  },
  activeCompany: {
    isFetching: true,
    data: {}
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
    case FETCH_COMPANY_REQUEST:
      return Object.assign({}, state, {
        activeCompany: {
          isFetching: action.payload.isFetching,
          error: null
        }
      })
    case FETCH_COMPANY_SUCCESS:
      return Object.assign({}, state, {
        activeCompany: {
          isFetching: action.payload.isFetching,
          data: action.payload.data,
          error: null
        }
      })
    case FETCH_COMPANY_FAILURE:
      return Object.assign({}, state, {
        activeCompany: {
          isFetching: action.payload.isFetching,
          error: action.payload.error
        }
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