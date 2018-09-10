import React from 'react';

const CredentialsTable = ({ credentials, onSetImpersonatedUser }) =>
  <div>
    <h4 className="homepage-title">Available Credentials</h4>
    <table className="table table-striped table-credentials">
      <thead>
        <tr>
          <th>Username</th>
          <th>Full Name</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        { credentials &&
            credentials.sort((a, b) => {
                return a.userName > b.userName
            }).map(user => {
                return (
                  <tr key={user.userName}>
                    <td className="text-info-axon">{user.userName}</td>
                    <td>{user.fullName}</td>
                    <td><button onClick={() => onSetImpersonatedUser(user)}>Impersonate User</button></td>
                  </tr>
                )
            })
        }
      </tbody>
    </table>
  </div>;

export default CredentialsTable;
