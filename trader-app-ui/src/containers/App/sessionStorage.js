export const loadState = () => {
  try {
    const serializedState = sessionStorage.getItem('axonState');
    if(serializedState == null) {
      return undefined;
    }
    const homeState = JSON.parse(serializedState);
    return {
      home: homeState
    }
  } catch(err) {
    return undefined;
  }
}

export const saveState = (state) => {
  try {
    // save state.home (authentication info) in sessionStorage
    const serializedState = JSON.stringify(state.home);
    sessionStorage.setItem('axonState', serializedState);
  } catch (err) {
    // ignore write errors
  }
}
