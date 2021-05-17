import React, { Component } from 'react';
import { Box, Typography, AppBar, Toolbar } from '@material-ui/core';
import ProcessDetails from './ProcessDetails'

class App extends Component {
  constructor(props) {
    super(props)
  }


  render() {
    const processDetails = (
      <ProcessDetails bulkProcess={'NEW_ADDRESS'}></ProcessDetails>
    )

    return (
      <Box>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" color="inherit">
              RM Bulk Processing
            </Typography>
          </Toolbar>
        </AppBar>
        {processDetails}
      </Box>
    )
  }
}

export default App