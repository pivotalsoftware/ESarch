import {
  FETCH_COMPANY_LIST_REQUEST,
  FETCH_COMPANY_LIST_SUCCESS,
  FETCH_COMPANY_LIST_FAILURE,
  FETCH_COMPANY_REQUEST,
  FETCH_COMPANY_SUCCESS,
  FETCH_COMPANY_FAILURE
} from '../constants/companyActions';

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

    // make the ajax request here
    // GET /companies
    // for now, mock the process

    const fakeResponseBody = {
      data: [
        {
          id: '001',
          name: 'Bp',
          value: '15000',
          shares: '100000'
        },
        {
          id: '002',
          name: 'Philips',
          value: '1000',
          shares: '10000'
        },
        {
          id: '003',
          name: 'Shell',
          value: '500',
          shares: '5000'
        }
      ]
    };

    setTimeout(
      () => dispatch(fetchCompanyListSuccess(fakeResponseBody.data)
      ), 800);

  }

const fetchCompanyRequest = () => (
  {
    type: FETCH_COMPANY_REQUEST,
    payload: {
      isFetching: true
    }
  }
)

const fetchCompanySuccess = data => (
  {
    type: FETCH_COMPANY_SUCCESS,
    payload: {
      isFetching: false,
      data
    }
  }
)
const fetchCompanyFailure = error => (
  {
    type: FETCH_COMPANY_FAILURE,
    payload: {
      isFetching: false,
      error
    }
  }
)

export const fetchCompany = (id) =>
  (dispatch) => {
    dispatch(fetchCompanyRequest());

    // make the ajax request here
    // GET companies/{id}
    // for now, mock the process

    const fakeResponseBody = {
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
    };

    setTimeout(
      () => dispatch(fetchCompanySuccess(fakeResponseBody.data)
      ), 800);

  }
