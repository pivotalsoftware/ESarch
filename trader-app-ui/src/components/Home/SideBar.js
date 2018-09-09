import React from 'react';
import SideBarItem from './SideBarItem';

const data = [
  {
    header: 'Check the stocks',
    description: 'If you have logged in, you can go to the companies',
    linkTo: '/companies',
    linkName: 'To the items'
  },
  {
    header: 'Executed trades',
    description: 'Trace all executed trades using the sockjs connection.',
    linkTo: '/orderbook',
    linkName: 'Executed trades'
  }
]

const SideBar = () =>
  <div>
    {
      data.map(item => {
        return(
          <SideBarItem
            key={item.header} 
            header={item.header}
            description={item.description}
            linkTo={item.linkTo}
            linkName={item.linkName}
          />
        )
      })
    }
  </div>

export default SideBar;
