import React from 'react';
import { Link } from 'react-router-dom';

const Banner = () => {
  return (
    <div>
      <h1 className="homepage-heading">The Trader</h1>
      <p className="text-homepage">
        Welcome to the Axon Trader application.
        Axon is the leading open source CQRS and Event Sourcing framework for
        &nbsp;<a href="http://projects.spring.io/spring-boot" className="homepage-link" target="_blank" rel="noopener noreferrer">Spring Boot</a>.
        &nbsp;Axon Trader showcases various <a href="http://www.axonframework.org/" className="homepage-link" target="_blank" rel="noopener noreferrer">Axon Framework</a>
        &nbsp;capabilities and it is built for <a href="https://pivotal.io/platform/pivotal-application-service" className="homepage-link" target="_blank" rel="noopener noreferrer">Pivotal Application Service</a> 
        &nbsp;(provided by <a href="https://run.pivotal.io" className="homepage-link" target="_blank" rel="noopener noreferrer">Pivotal Web Services</a>). 
        To get the code checkout <a href="https://github.com/pivotalsoftware/ESarch" className="homepage-link" target="_blank" rel="noopener noreferrer">this repository</a>.
        </p>
      <p className="text-info-homepage banner-info">
        Choose a user to impersonate below, then go to your account dashboard.
      </p>
      <Link className="btn btn-dashboard mt-5" to={'/dashboard'}>DASHBOARD</Link>
    </div>
  );
}

export default Banner;
