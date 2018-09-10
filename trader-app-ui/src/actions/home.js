import {
    FETCH_USERS_REQUEST,
    FETCH_USERS_SUCCESS,
    FETCH_USERS_FAILURE,
    SET_IMPERSONATED_USER
  } from '../constants/homeActions';
  import { status, json } from '../utils/fetch';
  
  const API_ROOT = process.env.REACT_APP_API_ROOT;
  
  const fetchUsersRequest = () => (
    {
      type: FETCH_USERS_REQUEST,
      payload: {
        isFetching: true
      }
    }
  )
  
  const fetchUsersSuccess = (data) => (
    {
      type: FETCH_USERS_SUCCESS,
      payload: {
        isFetching: false,
        data
      }
    }
  )
  
  const fetchUsersFailure = (error) => (
    {
      type: FETCH_USERS_FAILURE,
      payload: {
        isFetching: false,
        error
      }
    }
  )
  
  export const getUsers = () =>
    (dispatch) => {
  
      dispatch(fetchUsersRequest);
  
      const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      };
  
      return fetch(`${API_ROOT}/query/user`, options)
      .then(status)
      .then(json)
      .then((data) => {
        // got a successfull response from the server
        dispatch(fetchUsersSuccess(data));
      })
      .catch((error) => {
        // bad response
        dispatch(fetchUsersFailure(error));
      });
    }

    export function setImpersonatedUser(user) {
        // todo redirect to logout page
        return {
          type: SET_IMPERSONATED_USER,
          payload: {
            impersonatedUser: user
          }
        }
      }
      