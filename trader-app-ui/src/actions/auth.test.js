import { loginRequest, loginFailure } from './auth';
import * as actions from '../constants/authActions';

describe('authActions', () => {
  it('should create an action to request authentication', () => {
    const expectedAction = {
      type: actions.AUTHENTICATION_REQUEST,
      payload: {
        isFetching: true,
        isAuthenticated: false
      }
    }
    expect(loginRequest()).toEqual(expectedAction)
  })
})

describe('authActions', () => {
  it('should create an action to handle errors when logging in', () => {
    const error = {
      message: 'authentication failure', 
      status: 403
    }
    const expectedAction = {
      type: actions.AUTHENTICATION_FAILURE,
      payload: {
        isFetching: false,
        isAuthenticated: false,
        error
      }
    }
    expect(loginFailure(error)).toEqual(expectedAction)
  })
})
