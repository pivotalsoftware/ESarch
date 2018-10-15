import homeReducer from './home';
import * as actions from '../constants/homeActions';

describe('home reducer', () => {
  it('should return the initial state', () => {
    expect(homeReducer(undefined, {})).toEqual(
      {
        isFetching: false,
        error: null,
        users: [],
        impersonatedUser: null,
      },
    );
  });

  it('should handle FETCH_USERS_REQUEST', () => {
    expect(homeReducer({}, {
      type: actions.FETCH_USERS_REQUEST,
      payload: {
        isFetching: true,
      },
    })).toEqual(
      {
        isFetching: true,
        error: null,
      },
    );
  });

  it('should handle FETCH_USERS_SUCCESS', () => {
    expect(homeReducer({}, {
      type: actions.FETCH_USERS_SUCCESS,
      payload: {
        isFetching: true,
        data: [
          { identifier: '9834kdjshg', name: 'Buyer One' },
          { identifier: 'dfsdf34563', name: 'Buyer Two' },
        ],
      },
    })).toEqual(
      {
        isFetching: true,
        users: [
          { identifier: '9834kdjshg', name: 'Buyer One' },
          { identifier: 'dfsdf34563', name: 'Buyer Two' },
        ],
      },
    );
  });

  it('should handle FETCH_USERS_FAILURE', () => {
    expect(homeReducer({}, {
      type: actions.FETCH_USERS_FAILURE,
      payload: {
        isFetching: false,
        error: {
          message: 'Not Found',
        },

      },
    })).toEqual(
      {
        isFetching: false,
        error: {
          message: 'Not Found',
        },
      },
    );
  });

  it('should handle SET_IMPERSONATED_USER', () => {
    expect(homeReducer({}, {
      type: actions.SET_IMPERSONATED_USER,
      payload: {
        impersonatedUser: { identifier: 'kdjs333', name: 'Buyer One' },
      },
    })).toEqual(
      {
        impersonatedUser: { identifier: 'kdjs333', name: 'Buyer One' },
      },
    );
  });

  it('should handle LOGOUT_USER', () => {
    expect(homeReducer({}, {
      type: actions.LOGOUT_USER,
    })).toEqual(
      {
        impersonatedUser: null,
      },
    );
  });
});
