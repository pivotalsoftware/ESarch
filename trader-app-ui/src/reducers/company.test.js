import companyReducer from './company';
import * as actions from '../constants/companyActions';

describe('company reducer', () => {
  it('should return the initial state', () => {
    expect(companyReducer(undefined, {})).toEqual(
      {
        companyList: {
          items: []
        },
        activeCompany: {
          companyDetail: {
            isFetching: false,
            error: null,
            name: null,
            value: null,
            amountOfShares: null
          },
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
    )
  })

  it('should handle FETCH_COMPANY_REQUEST', () => {
    expect(companyReducer({}, {
      type: actions.FETCH_COMPANY_REQUEST,
    })).toEqual(
      {
        activeCompany: {
          companyDetail: {
            isFetching: true,
            error: null
          }
        }
      }
    )
  })

  it('should handle FETCH_COMPANY_SUCCESS', () => {
    expect(companyReducer({}, {
      type: actions.FETCH_COMPANY_SUCCESS,
      payload: {
        data: {
          identifier: 'dskj2378',
          name: 'Pivotal',
          value: '500',
          amountOfShares: '1000'
        }
      }
    })).toEqual(
      {
        activeCompany: {
          companyDetail: {
            isFetching: false,
            error: null,
            identifier: 'dskj2378',
            name: 'Pivotal',
            value: '500',
            amountOfShares: '1000'
          }
        }
      }
    )
  })

  it('should handle FETCH_COMPANY_FAILURE', () => {
    expect(companyReducer({}, {
      type: actions.FETCH_COMPANY_FAILURE,
      error: {
        message: 'Not Found'
      }
    })).toEqual(
      {
        activeCompany: {
          companyDetail: {
            isFetching: false,
            error: {
              message: 'Not Found'
            },
          }
        }
      }
    )
  })

  it('should handle FETCH_COMPANY_LIST_REQUEST', () => {
    expect(companyReducer({}, {
      type: actions.FETCH_COMPANY_LIST_REQUEST,
    })).toEqual(
      {
        companyList: {
          isFetching: true,
          error: null
        }
      }
    )
  })

  it('should handle FETCH_COMPANY_LIST_SUCCESS', () => {
    expect(companyReducer({}, {
      type: actions.FETCH_COMPANY_LIST_SUCCESS,
      payload: {
        data: [
          {identifier: '8293sdjh', name: 'Pivotal', value: 100},
          {identifier: '12hdi393', name: 'Solstice', value: 400},
        ]
      }
    })).toEqual(
      {
        companyList: {
          isFetching: false,
          error: null,
          items: [
            {identifier: '8293sdjh', name: 'Pivotal', value: 100},
            {identifier: '12hdi393', name: 'Solstice', value: 400},
          ]
        }
      }
    )
  })

  it('should handle FETCH_COMPANY_LIST_FAILURE', () => {
    expect(companyReducer({}, {
      type: actions.FETCH_COMPANY_LIST_FAILURE,
      payload: {
        error: {
          message: 'Not Found'
        }
      }
    })).toEqual(
      {
        companyList: {
          isFetching: false,
          error: {
            message: 'Not Found'
          }
        }
      }
    )
  })

  it('should handle FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST', () => {
    expect(companyReducer({activeCompany: {}}, {
      type: actions.FETCH_ORDERBOOKS_BY_COMPANYID_REQUEST,
    })).toEqual(
      {
        activeCompany: {
          orderBook: {
            isFetching: true
          }
        }
      }
    )
  })

  it('should handle FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS', () => {
    expect(companyReducer({activeCompany: {}}, {
      type: actions.FETCH_ORDERBOOKS_BY_COMPANYID_SUCCESS,
      payload: {
        data: [{
          identifier: 'kdsjhf38739',
          buyOrders: [],
          sellOrders: []
        }]
      }
    })).toEqual(
      {
        activeCompany: {
          orderBook: {
            isFetching: false,
            error: null,
            identifier: 'kdsjhf38739',
            buy: [],
            sell: []
          }
        }
      }
    )
  })

  it('should handle FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE', () => {
    expect(companyReducer({activeCompany: {}}, {
      type: actions.FETCH_ORDERBOOKS_BY_COMPANYID_FAILURE,
      payload: {
        error: {
          message: 'Not Found'
        }
      }
    })).toEqual(
      {
        activeCompany: {
          orderBook: {
            isFetching: false,
            error: {
              message: 'Not Found'
            },
            identifier: null,
            buy: [],
            sell: []
          }
        }
      }
    )
  })

  it('should handle SET_SSE_ORDERBOOK_DATA', () => {
    expect(companyReducer({activeCompany: {}}, {
      type: actions.SET_SSE_ORDERBOOK_DATA,
      payload: {
        data: {
          identifier: '8923kjdskj',
          buyOrders: [],
          sellOrders: []
        }
      }
    })).toEqual(
      {
        activeCompany: {
          orderBook: {
            isFetching: false,
            error: null,
            identifier: '8923kjdskj',
            buy: [],
            sell: []
          }
        }
      }
    )
  })

})
