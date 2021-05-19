import React, { Component } from 'react';
import '@fontsource/roboto';
import axios from 'axios';
import { Grid, Paper, Typography, Button, LinearProgress, Snackbar, SnackbarContent, Dialog, DialogContent, CircularProgress } from '@material-ui/core';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import JobDetails from './JobDetails';

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

    this.interval = setInterval(
      () => this.getJobs(),
      1000
    )
  }

  handleUpload = (e) => {
    // Display the progress modal dialog
    this.setState({
      uploadInProgress: true,
    })

    const formData = new FormData();
    formData.append("file", e.target.files[0]);
    formData.append("bulkProcess", this.props.bulkProcess['bulkProcess'])

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
    const response = await fetch('/job?bulkProcess=' + this.props.bulkProcess['bulkProcess'])
    const jobs = await response.json()

    this.setState({ jobs: jobs })
  }

  componentWillUnmount() {
    clearInterval(this.interval)
  }

  handleOpenDetails = (index) => {
    this.setState({ showDetails: true, selectedJob: index })
  }

  handleClosedDetails = () => {
    this.setState({ showDetails: false })
  }

  render() {
    const jobTableRows = this.state.jobs.map((job, index) => (
      <TableRow key={job.createdAt}>
        <TableCell component="th" scope="row">
          {job.fileName}
        </TableCell>
        <TableCell>{job.createdAt}</TableCell>
        <TableCell align="right">
          <Button
            onClick={() => this.handleOpenDetails(index)}
            variant="contained">
            {job.jobStatus} {!job.jobStatus.startsWith('PROCESSED') && <CircularProgress size={15} style={{ marginLeft: 10 }} />}
          </Button>
        </TableCell>
      </TableRow>
    ))

    return (
      <Grid>
        <Paper elevation={3} style={{ margin: 10, padding: 10 }}>
          <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
            Please upload a {this.props.bulkProcess['title']} bulk file for processing
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
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>File Name</TableCell>
                  <TableCell>Date Uploaded</TableCell>
                  <TableCell align="right">Status</TableCell>
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
        <JobDetails jobTitle={this.props.bulkProcess['title']} job={this.state.jobs[this.state.selectedJob]} showDetails={this.state.showDetails} handleClosedDetails={this.handleClosedDetails}>
        </JobDetails>
      </Grid>
    )
  }
}

export default ProcessDetails