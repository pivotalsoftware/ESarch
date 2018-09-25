import React from 'react';

const Header = () => {
  return (
    <div className="mt-4 mb-5">
      <h1 className="homepage-heading my-4">Welcome</h1>
      <p className="text-info-homepage header-info">Have fun playing with the trader</p>
      <div className="mt-1 small-divider-white home-extra-margin-left"/>
      <p className="text-homepage">
        There are a few things implemented.
        You can choose the company to trade stock shares for.
        Before you can use them you need to login.
      </p>
    </div>
  );
}

export default Header;
