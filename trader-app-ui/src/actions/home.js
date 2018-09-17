import {
    FETCH_USERS_REQUEST,
    FETCH_USERS_SUCCESS,
    FETCH_USERS_FAILURE,
    SET_IMPERSONATED_USER,
    LOGOUT_USER
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

  const validImpersonatedUser = (userList, currentImpersonatedUserId) => {
    for(let user of userList) {
      if(user.identifier === currentImpersonatedUserId) {
        return true;
      }
    }
    return false;
  }
  
  export const getUsers = () =>
    (dispatch, getState) => {

      const appState = getState();
  
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

        // verify the currently impersonated user is valid
        if(appState.home.impersonatedUser && appState.home.impersonatedUser.identifier){
          const impersonatedUserId = appState.home.impersonatedUser.identifier;
          if(!validImpersonatedUser(data, impersonatedUserId)){
            console.log('invalid impersonated user. logging out..')
            dispatch({
              type: LOGOUT_USER
            })
          }
        }
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
      