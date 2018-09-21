import {
  fetchUsersRequest,
  fetchUsersSuccess,
  fetchUsersFailure,
  validImpersonatedUser,
  setImpersonatedUser
} from './home';
import * as actions from '../constants/homeActions';

describe('homeActions: fetch users request', () => {
  it('should create an action to request fetching users', () => {
    const expectedAction = {
      type: actions.FETCH_USERS_REQUEST,
      payload: {
        isFetching: true
      }
    }
    expect(fetchUsersRequest()).toEqual(expectedAction)
  })
})

describe('homeActions: fetch users success', () => {
  it('should create an action to handle when user list fetched successfully', () => {
    const response = [
      {
        identifier: 'safhfh1234',
        name: 'Buyer One',
        userName: 'buyerOne'
      },
      {
        identifier: 'safhfh4dfgdf1234',
        name: 'Buyer Three',
        userName: 'buyerThree'
      },
    ]
    const expectedAction = {
      type: actions.FETCH_USERS_SUCCESS,
      payload: {
        isFetching: false,
        data: response
      }
    }
    expect(fetchUsersSuccess(response)).toEqual(expectedAction)
  })
})

describe('homeActions: fetch users failure', () => {
  it('should create an action to handle errors when fetching users', () => {
    const error = {
      message: 'Not Found',
      status: 404,
    }
    const expectedAction = {
      type: actions.FETCH_USERS_FAILURE,
      payload: {
        isFetching: false,
        error
      }
    }
    expect(fetchUsersFailure(error)).toEqual(expectedAction)
  })
})

describe('homeActions: validate currently impersonated user', () => {
  const userList = [
    {
      identifier: 'safhfh1234',
      name: 'Buyer One',
      userName: 'buyerOne'
    },
    {
      identifier: 'y6fh4dfgdf1234',
      name: 'Buyer Two',
      userName: 'buyerThree'
    },
    {
      identifier: 'fhfh4df43564gdf1234',
      name: 'Buyer Three',
      userName: 'buyerTwo'
    },
  ]
  it('should return true if user id is present in userlist', () => {
    expect(validImpersonatedUser(userList, 'fhfh4df43564gdf1234')).toEqual(true)
  })

  it('should return true if user id is present in userlist', () => {
    expect(validImpersonatedUser(userList, 'y6fh4dfgdf1234')).toEqual(true)
  })

  it('should return false if user id is not present in userlist', () => {
    expect(validImpersonatedUser(userList, '124hdhj438hj')).toEqual(false)
  })
})

describe('homeActions: set impersonated user', () => {
  it('should create an action to impersonate a user', () => {
    const user = {
      identifier: '4739048kjsdh',
      name: 'Buyer One',
      userName: 'buyerOne'
    }
    const expectedAction = {
      type: actions.SET_IMPERSONATED_USER,
      payload: {
        impersonatedUser: user
      }
    }
    expect(setImpersonatedUser(user)).toEqual(expectedAction)
  })
})
