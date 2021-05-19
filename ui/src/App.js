import React, { Component } from 'react';
import { Box, Typography, AppBar, Toolbar, Button } from '@material-ui/core';
import ProcessDetails from './ProcessDetails'
import ChooseProcess from './ChooseProcess'

class App extends Component {
  state = {
    selectedBulkProcess: null
  }

  onChooseProcess = (bulkProcess) => {
    this.setState({ selectedBulkProcess: bulkProcess })
  }

  onBackButton = () => {
    this.setState({ selectedBulkProcess: null })
  }

  render() {
    var processDetails

    if (this.state.selectedBulkProcess) {
      processDetails = (
        <ProcessDetails bulkProcess={this.state.selectedBulkProcess}></ProcessDetails>
      )
    }

    const chooseProcess = (
      <ChooseProcess onChooseProcess={this.onChooseProcess}></ChooseProcess>
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
        {processDetails &&
          <Button onClick={this.onBackButton}>
            Back
        </Button>
        }
        {processDetails}
        {!processDetails && chooseProcess}
      </Box>
    )
  }
}

export default App