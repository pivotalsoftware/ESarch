import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './containers/App';
import registerServiceWorker from './registerServiceWorker';

if (process.env.NODE_ENV !== 'production') {
  require('dotenv').load();
}

ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();
