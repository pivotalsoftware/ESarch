import React from 'react';
import { Link } from 'react-router-dom';

const Banner = () => {
  return (
    <div>
      <h1 className="homepage-heading">The Trader</h1>
      <p className="text-homepage">
        Welcome to the Axon Trader application. Axon is the leading open source CQRS and Event Sourcing framework for <a href="http://projects.spring.io/spring-boot">Spring Boot</a>. Axon Trader showcases various <a href="http://www.axonframework.org/">Axon Framework</a> capabilities and it is built for <a href="https://pivotal.io/platform/pivotal-application-service">Pivotal Application Service</a> (provided by <a href="https://run.pivotal.io">Pivotal Web Services</a>). To get the code checkout <a href="https://github.com/pivotalsoftware/ESarch">this repository</a>.
        </p>
      <p className="text-info-homepage banner-info">
        Choose a user to impersonate below, then go to your account dashboard.
      </p>
      <Link className="btn btn-dashboard mt-5" to={'/dashboard'}>DASHBOARD</Link>
    </div>
  );
}

export default Banner;
