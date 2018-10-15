import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import Loader from '../Loader';
import './styles.css';

class CompanyList extends Component {
  constructor(props) {
    super(props);
    this.sortClickHandler = this.sortClickHandler.bind(this);
    this.sortItems = this.sortItems.bind(this);
    this.state = {
      sortedBy: 'name',
      sortOrderAsc: true,
    };
  }

  static getDerivedStateFromProps(props, state) {
    return {
      items: props.companies.items,
    };
  }

  sortItems(items, column, asc) {
    if (column === 'name') {
      if (asc) {
        return items.sort((a, b) => {
          const nameA = a.name.toUpperCase();
          const nameB = b.name.toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        });
      }
      return items.sort((a, b) => {
        const nameA = a.name.toUpperCase();
        const nameB = b.name.toUpperCase();
        if (nameA < nameB) {
          return 1;
        }
        if (nameA > nameB) {
          return -1;
        }
        return 0;
      });
    }
    if (column === 'value') {
      if (asc) {
        return items.sort((a, b) => a.value - b.value);
      }
      return items.sort((a, b) => b.value - a.value);
    }
    if (column === 'shares') {
      if (asc) {
        return items.sort((a, b) => a.amountOfShares - b.amountOfShares);
      }
      return items.sort((a, b) => b.amountOfShares - a.amountOfShares);
    }
  }

  sortClickHandler(sortColumn) {
    const { sortedBy } = this.state;

    this.setState((prevState) => {
      const asc = sortedBy === sortColumn ? !prevState.sortOrderAsc : true;
      return {
        sortedBy: sortColumn,
        sortOrderAsc: asc,
        items: this.sortItems(prevState.items, sortColumn, asc),
      };
    });
  }

  render() {
    const { isFetching, error, } = this.props.companies;
    const { items, sortOrderAsc, sortedBy } = this.state;

    if (error) {
      return (
        <h1 className="axon-error">
Error loading page!
          {' '}
          {error.message}
        </h1>
      );
    }

    if (isFetching) {
      return <Loader className="centered-loader" />;
    }

    const sortArrowClassName = sortOrderAsc ? 'sort-indicator-ascending' : 'sort-indicator-descending';

    return (
      <div className="table-responsive">
        <table className="table table-bordered company-table">
          <thead>
            <tr className="list-row-gray">
              <th className="company-list-header" onClick={() => this.sortClickHandler('name')}>
                <span className="company-list-cell-text company-list-cell-text-semibold">Name</span>
                {sortedBy === 'name' && <div className={sortArrowClassName} />}
              </th>
              <th className="company-list-header" onClick={() => this.sortClickHandler('value')}>
                <span className="company-list-cell-text company-list-cell-text-semibold">Value</span>
                {sortedBy === 'value' && <div className={sortArrowClassName} />}
              </th>
              <th className="company-list-header" onClick={() => this.sortClickHandler('shares')}>
                <span className="company-list-cell-text company-list-cell-text-semibold"># Shares</span>
                {sortedBy === 'shares' && <div className={sortArrowClassName} />}
              </th>
              <th>&nbsp;</th>
            </tr>
          </thead>
          <tbody>
            {
              items.map((company, index) => {
                const className = index % 2 === 0 ? 'list-row-white' : 'list-row-gray';

                return (
                  <tr key={company.identifier} className={className}>
                    <td className="company-list-cell-text">{company.name}</td>
                    <td className="company-list-cell-text">{(company.value).toLocaleString('en', { style: 'currency', currency: 'USD' })}</td>
                    <td className="company-list-cell-text">{company.amountOfShares.toLocaleString('en')}</td>
                    <td className="text-centering">
                      <Link className="company-list-details-link" to={`/orderbooks/${company.identifier}`}>Details</Link>
                    </td>
                  </tr>
                );
              })
            }
          </tbody>
        </table>
      </div>
    );
  }
}

export default CompanyList;
