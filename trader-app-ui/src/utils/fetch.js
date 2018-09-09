const status = (response) => {
  // status 200 numbers are successfull http responses
  if (response.status >= 200 && response.status < 300) {
    return Promise.resolve(response);
  }
  return Promise.reject(new Error(response.statusText));
};

const json = response => response.json();

export { status, json };
