import React, { Component } from 'react';
import { Link } from 'react-router-dom';
// import classes from './styles.scss';

class CompanyList extends Component {
  constructor(props){
    super(props);
    this.sortClickHandler = this.sortClickHandler.bind(this);
    this.sortItems = this.sortItems.bind(this);
    this.state = {
      sortedBy: 'name',
      sortOrderAsc: false
    }
  }

  sortItems(items, column, asc){
    if(column === 'name') {

      if(asc) {
        return items.sort((a, b) => {
          var nameA = a.name.toUpperCase();
          var nameB = b.name.toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        });
      } else {
        return items.sort((a, b) => {
          var nameA = a.name.toUpperCase();
          var nameB = b.name.toUpperCase();
          if (nameA < nameB) {
            return 1;
          }
          if (nameA > nameB) {
            return -1;
          }
          return 0;
        });
      }
    }

  if(column === 'value') {
    if(asc) {
      return items.sort(function (a, b) {
        return a.value - b.value;
      });
    } else {
      return items.sort(function (a, b) {
        return b.value - a.value;
      });
    }
  }
  else if(column === 'shares') {
    if(asc) {
      return items.sort(function (a, b) {
        return a.shares - b.shares;
      });
    } else {
      return items.sort(function (a, b) {
        return b.shares - a.shares;
      });
    }
  }

}

  sortClickHandler(sortColumn) {
    const { sortedBy } = this.state;
    
    this.setState((prevState) => {
      const asc = sortedBy === sortColumn ? !prevState.sortOrderAsc : true
      return {
        sortedBy: sortColumn,
        sortOrderAsc: asc,
        items: this.sortItems(prevState.items, sortColumn, asc)
      }
    });
  }

  static getDerivedStateFromProps(props, state) {
    return {
      items: props.companies.items
    }
  }


  render() {

    const { isFetching, error } = this.props.companies;
    const { items } = this.state;

    if (error) {
      return <h1>Error loading page! {error.message}</h1>;
    }

    if (isFetching) {
      return <h1>Loading...</h1>;
    }  

    return (
      <table className="table">
        <thead>
          <tr>
            <th>
              Name
              <button className="btn btn-default" onClick={() => this.sortClickHandler('name')} >
                Toggle
              </button>
            </th>
            <th>
              Value
              <button className="btn btn-default"  onClick={() => this.sortClickHandler('value')}>
                Toggle
              </button>
            </th>
            <th>
              # Shares
              <button className="btn btn-default"  onClick={() => this.sortClickHandler('shares')}>
                Toggle
              </button>
            </th>
            <th>&nbsp;</th>
          </tr>
        </thead>
        <tbody>
          {
            items.map((company) => {
              return (
                <tr key={company.identifier}>
                  <td>{company.name}</td>
                  <td>{company.value}</td>
                  <td>{company.amountOfShares}</td>
                  <td>
                      <Link to={`/companies/${company.identifier}`}>details</Link>
                  </td>
                </tr>
              )
            })
          }
        </tbody>
      </table>
    )
  }
}

export default CompanyList;