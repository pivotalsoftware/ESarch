import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import "./styles.css";

class CompanyList extends Component {
  constructor(props){
    super(props);
    this.sortClickHandler = this.sortClickHandler.bind(this);
    this.sortItems = this.sortItems.bind(this);
    this.state = {
      sortedBy: 'name',
      sortOrderAsc: true
    }
  }

  sortItems(items, column, asc) {
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
    else if(column === 'value') {
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
          return a.amountOfShares - b.amountOfShares;
        });
      } else {
        return items.sort(function (a, b) {
          return b.amountOfShares - a.amountOfShares;
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

    const sortArrowClassName = this.state.sortOrderAsc ? "sort-indicator-ascending" : "sort-indicator-descending"

    return (
      <table className="table table-bordered company-table">
        <thead>
          <tr className="list-row-gray">
            <th className="company-list-header" onClick={() => this.sortClickHandler('name')}>
              <span className="company-list-cell-text company-list-cell-text-semibold">Name</span>
              {this.state.sortedBy === 'name' && <div className={sortArrowClassName}/>}
            </th>
            <th className="company-list-header" onClick={() => this.sortClickHandler('value')}>
              <span className="company-list-cell-text company-list-cell-text-semibold">Value</span>
              {this.state.sortedBy === 'value' && <div className={sortArrowClassName}/>}
            </th>
            <th className="company-list-header" onClick={() => this.sortClickHandler('shares')}>
              <span className="company-list-cell-text company-list-cell-text-semibold"># Shares</span>
              {this.state.sortedBy === 'shares' && <div className={sortArrowClassName}/>}
            </th>
            <th>&nbsp;</th>
          </tr>
        </thead>
        <tbody>
          {
            items.map((company, index) => {
              const className = index % 2 === 0 ? "list-row-white" : "list-row-gray"

              return (
                <tr key={company.identifier} className={className}>
                  <td className="company-list-cell-text">{company.name}</td>
                  <td className="company-list-cell-text">{(company.value / 100).toLocaleString('en', { style: 'currency', currency: 'USD' })}</td>
                  <td className="company-list-cell-text">{company.amountOfShares.toLocaleString('en')}</td>
                  <td className="text-centering">
                      <Link className="company-list-details-link" to={`/shares/${company.identifier}`}>Details</Link>
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