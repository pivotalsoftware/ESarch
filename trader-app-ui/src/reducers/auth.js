import {
  AUTHENTICATION_REQUEST,
  AUTHENTICATION_SUCCESS,
  AUTHENTICATION_FAILURE,
  PERFORM_LOGOUT
} from '../constants/authActions';

const initialState = {
  isFetching: false,
  isAuthenticated: false,
  error: null
}

export default function (state = initialState, action) {
  switch (action.type) {
    case AUTHENTICATION_REQUEST:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        isAuthenticated: action.payload.isAuthenticated,
        error: null
      }
    case AUTHENTICATION_SUCCESS:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        isAuthenticated: action.payload.isAuthenticated,
        user: {
          name: action.payload.name,
          username: action.payload.username,
          fullName: action.payload.fullName,
          userId: action.payload.userId,
        }
      }
    case AUTHENTICATION_FAILURE:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        isAuthenticated: action.payload.isAuthenticated,
        error: action.payload.error
      }
    case PERFORM_LOGOUT:
      return {
        ...state,
        isFetching: action.payload.isFetching,
        isAuthenticated: action.payload.isAuthenticated,
        user: null
      }
    default:
      return state;
  }
}