import React, { Component } from 'react';
import '@fontsource/roboto';
import axios from 'axios';
import { Button, Box, Grid, Paper, AppBar, Toolbar, Typography, LinearProgress, Snackbar, SnackbarContent, Dialog, DialogContent } from '@material-ui/core';

class Upload extends Component {

  render() {
    return (
      <Box>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" color="inherit">
              RM Bulk Processing
            </Typography>
          </Toolbar>
        </AppBar>
        <Grid>
          <Paper elevation={3} style={{ margin: 10, padding: 10 }}>
            <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>
              Please upload a bulk file for processing
            </Typography>
            <input
              accept="csv/*"
              style={{ display: 'none' }}
              id="contained-button-file"
              type="file"
              onChange={(e) => {
                this.props.handleUpload(e)
              }}
            />
            <label htmlFor="contained-button-file">
              <Button variant="contained" component="span">
                Upload
              </Button>
            </label>
          </Paper>
        </Grid>
        <Dialog open={this.props.uploadInProgress}>
          <DialogContent style={{ padding: 30 }}>
            <Typography variant="h6" color="inherit">
              Uploading file...
            </Typography>
            <LinearProgress
              variant="determinate"
              value={this.props.fileProgress * 100}
              style={{ marginTop: 20, marginBottom: 20, width: 400 }} />
            <Typography variant="h6" color="inherit">
              {Math.round(this.props.fileProgress * 100)}%
          </Typography>
          </DialogContent>
        </Dialog>
        <Snackbar
          open={this.props.fileUploadSuccess}
          autoHideDuration={6000}
          onClose={this.props.handleClose}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'left',
          }}>
          <SnackbarContent style={{ backgroundColor: '#4caf50' }}
            message={'File upload successful!'}
          />
        </Snackbar>
      </Box>
    )
  }
}


class App extends Component {
  constructor(props) {
    super(props)

    // The state holds any values which should initiate a re-rendering of the UI, if they change
    this.state = {
      fileProgress: 0,  // Percentage of the file uploaded
      fileUploadSuccess: false, // Flag to flash the snackbar message on the screen, when file uploads successfully
      uploadInProgress: false // Flag to display the file upload progress modal dialog
    }
  }
    handleUpload = (e) => {
    // Display the progress modal dialog
    this.setState({
      uploadInProgress: true,
    })

    const formData = new FormData();
    formData.append("file", e.target.files[0]);

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

  render() {
    return(
    <Upload fileProgress={this.state.fileProgress} fileUploadSuccess={this.state.fileUploadSuccess} uploadInProgress={this.state.uploadInProgress}
            handleClose={this.handleClose} handleUpload={this.handleUpload}/>
    )}
}

export default App