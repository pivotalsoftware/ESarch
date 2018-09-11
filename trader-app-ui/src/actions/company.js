import {
  FETCH_COMPANY_LIST_REQUEST,
  FETCH_COMPANY_LIST_SUCCESS,
  FETCH_COMPANY_LIST_FAILURE,
  FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST,
  FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
  FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
  FETCH_EXECUTED_TRADES_REQUEST,
  FETCH_EXECUTED_TRADES_SUCCESS,
  FETCH_EXECUTED_TRADES_FAILURE,
  SET_ACTIVE_COMPANY
} from '../constants/companyActions';
import { status, json } from '../utils/fetch';

const API_ROOT = process.env.REACT_APP_API_ROOT;

const fetchCompanyListRequest = () => (
  {
    type: FETCH_COMPANY_LIST_REQUEST,
    payload: {
      isFetching: true
    }
  }
)

const fetchCompanyListSuccess = data => (
  {
    type: FETCH_COMPANY_LIST_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)

const fetchCompanyListFailure = error => (
  {
    type: FETCH_COMPANY_LIST_FAILURE,
    payload: {
      isFetching: false,
      error
    }
  }
)

export const fetchCompanyList = () =>
  (dispatch) => {
    dispatch(fetchCompanyListRequest());
    const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
    };
  
    return fetch(`${API_ROOT}/query/company`, options)
    .then(status)
    .then(json)
    .then((data) => {
      // got a successfull response from the server
      dispatch(fetchCompanyListSuccess(data));
    })
    .catch((error) => {
      // bad response
      console.log("Get Company List Failure", error);
      dispatch(fetchCompanyListFailure(error));
    });  
}

export const setActiveCompany = (id) => 
  (dispatch, getState) => {
    const state = getState();
    const index = state.companies.companyList.items.findIndex(element => {
      if(element.identifier ===id) {
        return true;
      }
      return false;
    })
    dispatch({
      type: SET_ACTIVE_COMPANY,
      payload: {
        index
      }})
  }

const fetchOrderBooksByCompanyIdRequest = () => (
  {
    type: FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST
  }
)

const fetchOrderBooksByCompanyIdSuccess = data => (
  {
    type: FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)
const fetchOrderBooksByCompanyIdFailure = error => (
  {
    type: FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
    payload: {
      isFetching: false,
      error
    }
  }
)

const fetchExecutedTradesRequest = () => {
  return {
    type: FETCH_EXECUTED_TRADES_REQUEST
  }
}

const fetchExecutedTradesSuccess = (data) => {
  return {
    type: FETCH_EXECUTED_TRADES_SUCCESS,
    payload: {
      data
    }
  }
}

const fetchExecutedTradesFailure = (error) => {
  return {
    type: FETCH_EXECUTED_TRADES_FAILURE,
    payload: {
      error
    }
  }
}

export const fetchOrderBooksByCompanyId = (id) =>
  (dispatch) => {
    dispatch(fetchOrderBooksByCompanyIdRequest());
    const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
    };
  
    return fetch(`${API_ROOT}/query/order-book/by-company/{id}}`, options)
    .then(status)
    .then(json)
    .then((data) => {
      // got a successfull response from the server
      const mockData = {
        identifier: '38209d',
        sellOrders: [{
          tradeCount: 5,
          itemPrice: 50,
          itemsRemaining: 35
        }],
        buyOrders: [{
          tradeCount: 5,
          itemPrice: 50,
          itemsRemaining: 35
        }]
      }

      dispatch(fetchOrderBooksByCompanyIdSuccess(mockData));
    })
    .catch((error) => {
      // bad response
      console.log("Get order book by company failure:", error);
      dispatch(fetchOrderBooksByCompanyIdFailure(error));
    });  
  }

  export const fetchExecutedTradesByOrderBookId = (id) => {
    return (dispatch) => {
      dispatch(fetchExecutedTradesRequest());
      const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      };

      return fetch(`${API_ROOT}/query/executed-trades/${id}`, options)
        .then(status)
        .then(json)
        .then(data => {
          const mockData = [
            {
              tradeCount: 30,
              tradePrice: 35
            },
            {
              tradeCount: 42,
              tradePrice: 500
            }
          ]
          dispatch(fetchExecutedTradesSuccess(mockData))
          console.log('success')
        })
        .catch(error => {
          dispatch(fetchExecutedTradesFailure(error))
          console.log('error')
        })
    }
  }
