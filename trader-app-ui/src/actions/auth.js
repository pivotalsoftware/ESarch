import { push } from "react-router-redux";
import {
  AUTHENTICATION_REQUEST,
  AUTHENTICATION_SUCCESS,
  AUTHENTICATION_FAILURE,
  PERFORM_LOGOUT
} from '../constants/authActions';
import { status, json } from '../utils/fetch';


const registeredUsers = ["buyer1", "buyer2", "buyer3", "buyer4", "buyer5", "buyer6"];
const API_ROOT = process.env.REACT_APP_API_ROOT;

export function performLogin(username, password) {
  return (dispatch) => {
    // TODO Service call for login
    dispatch(loginRequest());

    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    };

    return fetch(`${API_ROOT}/query/user/by-name/${username}`, options)
    .then(status)
    .then(json)
    .then((data) => {
      // got a successfull response from the server
      dispatch(loginSuccess(data));
      dispatch(push('/dashboard'));
    })
    .catch((error) => {
      // bad response
      console.log(error);
      dispatch(loginFailure({message: 'Invalid Credentials'}));
    });
  }
}

export function loginRequest() {
  return {
    type: AUTHENTICATION_REQUEST,
    payload: {
      isFetching: true,
      isAuthenticated: false
    }
  }
}

export function loginSuccess(data) {
  return {
    type: AUTHENTICATION_SUCCESS,
    payload: {
      isFetching: false,
      isAuthenticated: true,
      name: data.name,
      username: data.username,
      fullName: data.fullName,
      userId: data.userId,
    }
  }
}

export function loginFailure(error) {
  return {
    type: AUTHENTICATION_FAILURE,
    payload: {
      isFetching: false,
      isAuthenticated: false,
      error

    }
  }
}


export function performLogout() {
  // todo redirect to logout page
  return {
    type: PERFORM_LOGOUT,
    payload: {
      isFetching: false,
      isAuthenticated: false
    }
  }
}

