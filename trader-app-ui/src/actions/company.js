import {
  FETCH_COMPANY_LIST_REQUEST,
  FETCH_COMPANY_LIST_SUCCESS,
  FETCH_COMPANY_LIST_FAILURE,
  FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST,
  FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
  FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
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

export function setActiveCompany(company) {
    // todo redirect to logout page
    return {
      type: SET_ACTIVE_COMPANY,
      payload: {
        activeCompany: company
      }
    }
}

const fetchOrderBooksByCompanyIdRequest = () => (
  {
    type: FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST,
    payload: {
      isFetching: true
    }
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
      dispatch(fetchOrderBooksByCompanyIdSuccess(data));
    })
    .catch((error) => {
      // bad response
      console.log("Get order book by company failure:", error);
      dispatch(fetchOrderBooksByCompanyIdFailure(error));
    });  

  }
