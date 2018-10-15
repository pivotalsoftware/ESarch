const formatTransactionState = (state) => {
  const formattedState = state.toLowerCase()
    .split('_')
    .map(s => s.charAt(0).toUpperCase() + s.substring(1))
    .join(' ');

  return formattedState;
};

export default formatTransactionState;
