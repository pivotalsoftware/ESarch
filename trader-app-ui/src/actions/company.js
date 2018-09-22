import {
  FETCH_COMPANY_REQUEST,
  FETCH_COMPANY_SUCCESS,
  FETCH_COMPANY_FAILURE,
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
  SET_SSE_ORDERBOOK_DATA
} from '../constants/companyActions';
import { status, json } from '../utils/fetch';
import { ApiConfig } from '../utils/config';

const API_ROOT = ApiConfig();

export const fetchCompanyByIdRequest = () => (
  {
    type: FETCH_COMPANY_REQUEST
  }
)

export const fetchCompanyByIdSuccess = (data) => (
  {
    type: FETCH_COMPANY_SUCCESS,
    payload: {
      data
    }
  }
)

export const fetchCompanyByIdFailure = (error) => (
  {
    type: FETCH_COMPANY_FAILURE,
    error
  }
)

export const getCompanyFromCompanyList = (id, companyList) => {
  for(let company of companyList) {
    if(company.identifier === id) {
      return company;
    }
  }
  return null;
}

export const fetchCompanyById = (id) => 
  (dispatch, getState) => {
    const appState = getState();
    const companies = appState.companies.companyList
    let activeCompany;
    if(companies.items) {
      activeCompany = getCompanyFromCompanyList(id, companies.items)
    }

    // if company already present in the state, return that
    if(activeCompany) {
      return dispatch(fetchCompanyByIdSuccess(activeCompany))
    } else {
      dispatch(fetchCompanyByIdRequest());
      const options = {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
      };
  
      return fetch(`${API_ROOT}/query/company/${id}`, options)
      .then(status)
      .then(json)
      .then((data) => {
        // got a successfull response from the server
        dispatch(fetchCompanyByIdSuccess(data));
      })
      .catch((error) => {
        // bad response
        console.log("Get Company Failure", error);
        dispatch(fetchCompanyByIdFailure(error));
      })
    }
  }

export const fetchCompanyListRequest = () => (
  {
    type: FETCH_COMPANY_LIST_REQUEST,
    payload: {
      isFetching: true
    }
  }
)

export const fetchCompanyListSuccess = data => (
  {
    type: FETCH_COMPANY_LIST_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)

export const fetchCompanyListFailure = error => (
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

export const fetchOrderBooksByCompanyIdRequest = () => (
  {
    type: FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST
  }
)

export const fetchOrderBooksByCompanyIdSuccess = data => (
  {
    type: FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)
export const fetchOrderBooksByCompanyIdFailure = error => (
  {
    type: FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
    payload: {
      isFetching: false,
      error
    }
  }
)

const placeBuyOrderRequest = () => {
  return {
    type: PLACE_BUY_ORDER_REQUEST
  }
}

const placeBuyOrderSuccess = (transactionId) => {
  return {
    type: PLACE_BUY_ORDER_SUCCESS,
    payload: {
      transactionId
    }
  }
}

const placeBuyOrderFailure = (error) => {
  return {
    type: PLACE_BUY_ORDER_FAILURE,
    payload: {
      error
    }
  }
}

const placeSellOrderRequest = () => {
  return {
    type: PLACE_SELL_ORDER_REQUEST
  }
}

const placeSellOrderSuccess = (transactionId) => {
  return {
    type: PLACE_SELL_ORDER_SUCCESS,
    payload: {
      transactionId
    }
  }
}

const placeSellOrderFailure = (error) => {
  return {
    type: PLACE_SELL_ORDER_FAILURE,
    payload: {
      error
    }
  }
}

export const setSSEOrderBookData = (data) => (
  {
    type: SET_SSE_ORDERBOOK_DATA,
    payload: {
      data
    }
  }
)

export const fetchOrderBooksByCompanyId = (id) =>
  (dispatch) => {
    dispatch(fetchOrderBooksByCompanyIdRequest());
    const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
    };
  
    return fetch(`${API_ROOT}/query/order-book/by-company/${id}`, options)
    .then(status)
    .then(json)
    .then((data) => {
      // got a successfull response from the server
      dispatch(fetchOrderBooksByCompanyIdSuccess(data));
    })
    .catch((error) => {
      // bad response
      console.log("Get order book by company failure:", error);
      dispatch(fetchOrderBooksByCompanyIdFailure(error));
    });  
  }

export const placeBuyOrder = (orderBookId, portfolioId, price, amount) => {
  return (dispatch) => {
    dispatch(placeBuyOrderRequest());

    const options = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        orderBookId,
        portfolioId,
        tradeCount: amount,
        pricePerItem: price
      })
    };

    return fetch(`${API_ROOT}/command/StartBuyTransactionCommand`, options)
      .then(status)
      .then(json)
      .then(transactionId => {
        console.log('place buy order success: ' + transactionId)
        dispatch(placeBuyOrderSuccess(transactionId))
      })
      .catch(error => {
        console.log('place buy order error', JSON.stringify(error, null, 4))
        dispatch(placeBuyOrderFailure(error))
      })
  }
}

export const placeSellOrder = (orderBookId, portfolioId, price, amount) => {
  return (dispatch) => {
    dispatch(placeSellOrderRequest());

    const options = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        orderBookId,
        portfolioId,
        tradeCount: amount,
        pricePerItem: price
      })
    };

    return fetch(`${API_ROOT}/command/StartSellTransactionCommand`, options)
      .then(status)
      .then(json)
      .then(transactionId => {
        console.log('place sell order success: ' + transactionId)
        dispatch(placeSellOrderSuccess(transactionId))
      })
      .catch(error => {
        console.log('place sell order error', JSON.stringify(error, null, 4))
        dispatch(placeSellOrderFailure(error))
      })
  }
}

