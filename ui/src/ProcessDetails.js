import React, { Component } from 'react';
import '@fontsource/roboto';
import axios from 'axios';
import { Grid, Paper, Typography, Button, LinearProgress, Snackbar, SnackbarContent, Dialog, DialogContent } from '@material-ui/core';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

class ProcessDetails extends Component {
  constructor(props) {
    super(props)

    this.state = {
      jobs: [],
      fileProgress: 0,  // Percentage of the file uploaded
      fileUploadSuccess: false, // Flag to flash the snackbar message on the screen, when file uploads successfully
      uploadInProgress: false // Flag to display the file upload progress modal dialog
    }

    this.getJobs()
  }

  handleUpload = (e) => {
    // Display the progress modal dialog
    this.setState({
      uploadInProgress: true,
    })

    const formData = new FormData();
    formData.append("file", e.target.files[0]);
    formData.append("bulkProcess", this.props.bulkProcess)

    // Send the file data to the backend
    axios.request({
      method: "post",
      url: "/upload",
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (p) => {
        console.log(p);

        // Update file upload progress
        this.setState({
          fileProgress: p.loaded / p.total
        })
      }

    }).then(data => {
      // Hide the progress dialog and flash the snackbar message
      this.setState({
        fileProgress: 1.0,
        fileUploadSuccess: true,
        uploadInProgress: false,
      })

      this.getJobs()
    })
  }

  handleClose = (event, reason) => {
    // Ignore clickaways so that the dialog is modal
    if (reason === 'clickaway') {
      return;
    }

    this.setState({
      fileUploadSuccess: false,
    })
  }

  getJobs = async () => {
    const response = await fetch('/job?bulkProcess=' + this.props.bulkProcess)
    const jobs = await response.json()

    this.setState({ jobs: jobs })
    // alert(this.state.jobs.length)
  }

  render() {
    const jobTableRows = this.state.jobs.map(job => (
      <TableRow key={job.createdAt}>
        <TableCell component="th" scope="row">
          {job.fileName}
        </TableCell>
        <TableCell align="right">{job.createdAt}</TableCell>
        <TableCell align="right">{job.jobStatus}</TableCell>
      </TableRow>
    ))

    return (
      <Grid>
        <Paper elevation={3} style={{ margin: 10, padding: 10 }}>
          <Paper elevation={3} style={{ margin: 10, padding: 10 }}>
            <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
              Please upload a {this.props.bulkProcess} bulk file for processing
            </Typography>
            <input
              accept="csv/*"
              style={{ display: 'none' }}
              id="contained-button-file"
              type="file"
              onChange={(e) => {
                this.handleUpload(e)
              }}
            />
            <label htmlFor="contained-button-file">
              <Button variant="contained" component="span">
                Upload
              </Button>
            </label>
          </Paper>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>File Name</TableCell>
                  <TableCell align="right">Date Uploaded</TableCell>
                  <TableCell align="right">Progress</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {jobTableRows}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
        <Dialog open={this.state.uploadInProgress}>
          <DialogContent style={{ padding: 30 }}>
            <Typography variant="h6" color="inherit">
              Uploading file...
            </Typography>
            <LinearProgress
              variant="determinate"
              value={this.state.fileProgress * 100}
              style={{ marginTop: 20, marginBottom: 20, width: 400 }} />
            <Typography variant="h6" color="inherit">
              {Math.round(this.state.fileProgress * 100)}%
          </Typography>
          </DialogContent>
        </Dialog>
        <Snackbar
          open={this.state.fileUploadSuccess}
          autoHideDuration={6000}
          onClose={this.handleClose}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'left',
          }}>
          <SnackbarContent style={{ backgroundColor: '#4caf50' }}
            message={'File upload successful!'}
          />
        </Snackbar>
      </Grid>
    )
  }
}

export default ProcessDetails