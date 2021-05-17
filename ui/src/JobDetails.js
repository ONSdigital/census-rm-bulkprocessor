import React, { Component } from 'react';
import '@fontsource/roboto';
import { Typography, Grid, Button, Dialog, DialogContent } from '@material-ui/core';


class JobDetails extends Component {









  render() {

    var jobDetailsFragment
    if (this.props.job) {
      jobDetailsFragment = (
        <Grid container spacing={1}>
        <Grid container item xs={12} spacing={3}>
          <Typography variant="h5" color="inherit" style={{ margin: 10, padding: 10 }}>
            New Address
      </Typography>
        </Grid>
        <Grid container item xs={12} spacing={3}>
          <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
            File: {this.props.job.fileName}
          </Typography>
        </Grid>
        <Grid container item xs={12} spacing={3}>
          <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
            Processing Progress: {this.props.job.jobStatus}
          </Typography>
        </Grid>
        <Grid container item xs={12} spacing={3}>
          <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
            Errors: {this.props.job.rowErrorCount}
          </Typography>
        </Grid>
        <Grid container item xs={12} spacing={3}>
          <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
            Success: {this.props.job.rowCount}
          </Typography>

        </Grid>
        <Grid container item xs={12} spacing={3}>
          <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
            Uploaded Date/Time: {this.props.job.createdAt}
          </Typography>
        </Grid>
        </Grid>
      )
    }

    return (

      <Dialog open={this.props.showDetails}>
        <DialogContent style={{ padding: 30 }}>
          <Grid container spacing={1}>
          {jobDetailsFragment}
            <Button variant="contained" style={{ margin: 10 }}>
              Test 1
            </Button>
            <Button variant="contained" style={{ margin: 10 }}>
              Test 2
            </Button>
            <Button onClick={this.props.handleClosedDetails} variant="contained" style={{ margin: 10 }}>
              EXIT
            </Button>
          </Grid>
        </DialogContent>
      </Dialog>

    )
  }
}

export default JobDetails