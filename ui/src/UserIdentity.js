import React, { Component } from 'react';
import { Typography } from '@material-ui/core';

class UserIdentity extends Component {
  state = {
    user: 'Loading username...'
  }

  componentDidMount() {
    this.getUser()
  }

  getUser = async () => {
    const response = await fetch('whoami')
    const json = await response.json()
    this.setState({user: json.user})
  }

  render() {
    return (
      <div>
        <Typography variant="inherit" color="inherit" style={{ margin: 10, padding: 10 }}>
          {this.state.user}
        </Typography>
      </div>
    )
  }
}

export default UserIdentity