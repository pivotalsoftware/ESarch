import {
  fetchCompanyByIdRequest,
  fetchCompanyByIdSuccess,
  fetchCompanyByIdFailure,
  getCompanyFromCompanyList,
  fetchCompanyListRequest,
  fetchCompanyListSuccess,
  fetchCompanyListFailure,
  fetchOrderBooksByCompanyIdRequest,
  fetchOrderBooksByCompanyIdSuccess,
  fetchOrderBooksByCompanyIdFailure
} from './company';
import * as actions from '../constants/companyActions';

describe('companyActions: fetch company by id request', () => {
  it('should create an action to request fetching a company', () => {
    const expectedAction = {
      type: actions.FETCH_COMPANY_REQUEST,
    }
    expect(fetchCompanyByIdRequest()).toEqual(expectedAction)
  })
})

describe('companyActions: fetch company by id success', () => {
  it('should create an action to handle when a company is fetched successfully', () => {
    const response = {
      identifier: 1234,
      name: 'Solstice',
      amountOfShares: 10000
    }
    const expectedAction = {
      type: actions.FETCH_COMPANY_SUCCESS,
      payload: {
        data: response
      }
    }
    expect(fetchCompanyByIdSuccess(response)).toEqual(expectedAction)
  })
})

describe('companyActions: fetch company by id failure', () => {
  it('should create an action to handle errors when fetching a company by id', () => {
    const error = {
      message: 'Not Found',
      status: 404,
    }
    const expectedAction = {
      type: actions.FETCH_COMPANY_FAILURE,
      error
    }
    expect(fetchCompanyByIdFailure(error)).toEqual(expectedAction)
  })
})

describe('companyActions: util function', () => {
  const companyList = [
    {
      identifier: '1234abcd672',
      name: 'Solstice'
    },
    {
      identifier: '9028391absh33',
      name: 'Pivotal'
    },
    {
      identifier: '84hdjlsy7',
      name: 'Axon IQ'
    }
  ]
  it('should return company if id already present in company list', () => {
    const expectedOutput = {
      identifier: '9028391absh33',
      name: 'Pivotal'
    }

    expect(getCompanyFromCompanyList('9028391absh33', companyList)).toEqual(expectedOutput)
  })

  it('should return null if company is not present in the company list', () => {
    expect(getCompanyFromCompanyList('1111112233aabbcc', companyList)).toEqual(null)
  })

  it('should return null if company is not present in the company list', () => {
    expect(getCompanyFromCompanyList('84hdjlsy7A', companyList)).toEqual(null)
  })
})


describe('companyActions: fetch company list request', () => {
  it('should create an action to request fetching companies', () => {
    const expectedAction = {
      type: actions.FETCH_COMPANY_LIST_REQUEST,
      payload: {
        isFetching: true
      }
    }
    expect(fetchCompanyListRequest()).toEqual(expectedAction)
  })
})

describe('companyActions: fetch company list success', () => {
  it('should create an action to handle when a company is fetched successfully', () => {
    const response = [
      {
        identifier: 1234,
        name: 'Solstice',
        amountOfShares: 10000
      },
      {
        identifier: 3266,
        name: 'Pivotal',
        amountOfShares: 2000
      },
    ]
    const expectedAction = {
      type: actions.FETCH_COMPANY_LIST_SUCCESS,
      payload: {
        isFetching: false,
        data: response
      }
    }
    expect(fetchCompanyListSuccess(response)).toEqual(expectedAction)
  })
})

describe('companyActions: fetch company list failure', () => {
  it('should create an action to handle errors when fetching companies', () => {
    const error = {
      message: 'Not Found',
      status: 404,
    }
    const expectedAction = {
      type: actions.FETCH_COMPANY_LIST_FAILURE,
      payload: {
        isFetching: false,
        error
      }
    }
    expect(fetchCompanyListFailure(error)).toEqual(expectedAction)
  })
})

describe('companyActions: fetch Order Books By CompanyId Request', () => {
  it('should create an action to request fetching orderbooks', () => {
    const expectedAction = {
      type: actions.FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST,
    }
    expect(fetchOrderBooksByCompanyIdRequest()).toEqual(expectedAction)
  })
})

describe('companyActions: fetch Order Books By CompanyId success', () => {
  it('should create an action to handle when orderbooks are fetched successfully', () => {
    const response = [
      {
        identifier: '12fdgg34',
        buyOrders: [],
        sellOrders: []
      },
    ]
    const expectedAction = {
      type: actions.FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
      payload: {
        isFetching: false,
        data: response
      }
    }
    expect(fetchOrderBooksByCompanyIdSuccess(response)).toEqual(expectedAction)
  })
})

describe('companyActions: fetch Order Books By CompanyId failure', () => {
  it('should create an action to handle errors when fetching orderbooks', () => {
    const error = {
      message: 'Not Found',
      status: 404,
    }
    const expectedAction = {
      type: actions.FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
      payload: {
        isFetching: false,
        error
      }
    }
    expect(fetchOrderBooksByCompanyIdFailure(error)).toEqual(expectedAction)
  })
})
