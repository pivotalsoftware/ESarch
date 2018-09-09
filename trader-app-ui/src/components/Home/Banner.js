import React from 'react';
import { Link } from 'react-router-dom';

const Banner = () => {
  return (
    <div>
      <h1 className="homepage-heading mt-5 mb-4">The Trader</h1>
      <p className="text-homepage">
        Welcome to the proof of concept of Axon Trader. This sample is
        created to showcase axon capabilities. Next to
        that we wanted to create a cool app with a nice front-end that we
        can really use as a showcase.
      </p>
      <p className="text-info-homepage banner-info">
        If you are logged in, you can go to your dashboard.
      </p>
      <Link className="btn btn-dashboard my-4" to={'/dashboard'}>DASHBOARD</Link>
    </div>
  );
}

export default Banner;
