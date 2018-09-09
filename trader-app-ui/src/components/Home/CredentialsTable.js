import React from 'react';

const CredentialsTable = ({ credentials }) =>
  <div>
    <h4 className="homepage-title">Available Credentials</h4>
    <table className="table table-striped table-credentials">
      <thead>
        <tr>
          <th>User</th>
          <th>Password</th>
        </tr>
      </thead>
      <tbody>
        {
          credentials.map(user => {
            return (
              <tr key={user.userName}>
                <td className="text-info-axon">{user.userName}</td>
                <td>{user.password}</td>
              </tr>
            )
          })
        }
      </tbody>
    </table>
  </div>;

export default CredentialsTable;
