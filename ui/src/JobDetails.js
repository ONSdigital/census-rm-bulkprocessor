import React, { Component } from 'react';
import '@fontsource/roboto';
import { Typography, Grid, Button, Dialog, DialogContent } from '@material-ui/core';


class JobDetails extends Component {
  constructor(props) {
    super(props)
    this.state = {
      job: {
        "id": "19864513-eca0-4d34-860e-4b71d9fc03af", "bulkProcess": "NEW_ADDRESS",
        "createdAt": "2021-05-17T11:32:10.106716+01:00", "lastUpdatedAt": "2021-05-17T11:32:11.631205+01:00",
        "fileName": "1_per_treatment_code.csv", "jobStatus": "FILE_UPLOADED", "rowCount": 47, "rowErrorCount": 0,
        "fatalErrorDescription": null
      },
    }


  }






  render() {

    return (

      <Dialog open={this.props.showDetails}>
        <DialogContent style={{ padding: 30 }}>

          <Grid container spacing={1}>
            <Grid container item xs={12} spacing={3}>
              <Typography variant="h5" color="inherit" style={{ margin: 10, padding: 10 }}>
                New Address
            </Typography>
            </Grid>
            <Grid container item xs={12} spacing={3}>
              <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
                File: {this.state.job.fileName}
              </Typography>
            </Grid>
            <Grid container item xs={12} spacing={3}>
              <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
                Processing Progress: {this.state.job.jobStatus}
              </Typography>
            </Grid>
            <Grid container item xs={12} spacing={3}>
              <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
                Errors: {this.state.job.rowErrorCount}
              </Typography>
            </Grid>
            <Grid container item xs={12} spacing={3}>
              <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
                Success: {this.state.job.rowCount}
              </Typography>
            </Grid>
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