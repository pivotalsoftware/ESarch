export const loadState = () => {
  try {
    const serializedHomeState = sessionStorage.getItem('axonHomeState');
    const serializedPortfolioState = sessionStorage.getItem('axonPortfolioState');
    let initialState = {};

    if(serializedHomeState == null && serializedPortfolioState== null) {
      return undefined;
    }

    if(serializedHomeState) {
      initialState.home = JSON.parse(serializedHomeState);
    }

    if(serializedPortfolioState) {
      initialState.portfolio = JSON.parse(serializedPortfolioState);
    }
    return initialState;
  } catch(err) {
    return undefined;
  }
}

export const saveState = (state) => {
  try {
    // save state.home (authentication info) in sessionStorage
    const serializedHomeState = JSON.stringify(state.home);
    sessionStorage.setItem('axonHomeState', serializedHomeState);

    const serializedPortfolioState = JSON.stringify(state.portfolio);
    sessionStorage.setItem('axonPortfolioState', serializedPortfolioState);

  } catch (err) {
    // ignore write errors
  }
}
