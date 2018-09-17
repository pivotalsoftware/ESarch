import {
  FETCH_USERS_REQUEST,
  FETCH_USERS_SUCCESS,
  FETCH_USERS_FAILURE,
  SET_IMPERSONATED_USER,
  LOGOUT_USER
} from '../constants/homeActions';

const initialState = {
  isFetching: false,
  error: null,
  users: [],
  impersonatedUser: null
}

export default function homeReducer(state = initialState, action) {
  switch (action.type) {
    case FETCH_USERS_REQUEST:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        error: null
      }
    case FETCH_USERS_SUCCESS:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        users: action.payload.data
      }
    case FETCH_USERS_FAILURE:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        error: action.payload.error
      }
    case SET_IMPERSONATED_USER:
      return {
        ...state,
        impersonatedUser: action.payload.impersonatedUser
      }
    case LOGOUT_USER:
      return {
        ...state,
        impersonatedUser: null
      }
    default:
      return state
  }
}
