import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './containers/App';
import registerServiceWorker from './registerServiceWorker';
import dotenv from 'dotenv';

dotenv.config()

ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();
