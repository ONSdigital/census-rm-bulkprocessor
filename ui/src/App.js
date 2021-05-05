import React, { Component } from 'react';
import '@fontsource/roboto';
import axios from 'axios';
import { Button, Box, Grid, Paper, AppBar, Toolbar, Typography, LinearProgress, Snackbar, SnackbarContent, Dialog, DialogContent } from '@material-ui/core';

class App extends Component {
  constructor() {
    super();
    this.state = {
      fileProgress: 0,
      fileUploadSuccess: false,
      uploadInProgress: false
    }
  }

  handleUpload = (e) => {
    this.setState({
      uploadInProgress: true,
    })

    var formData = new FormData();
    formData.append("file", e.target.files[0]);

    axios.request({
      method: "post",
      url: "/upload",
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (p) => {
        console.log(p);
        this.setState({
          fileProgress: p.loaded / p.total
        })
      }

    }).then(data => {
      this.setState({
        fileProgress: 1.0,
        fileUploadSuccess: true,
        uploadInProgress: false,
      })
    })
  }

  handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }

    this.setState({
      fileUploadSuccess: false,
    })
  };

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
                this.handleUpload(e)
              }}
            />
            <label htmlFor="contained-button-file">
              <Button variant="contained" component="span">
                Upload
              </Button>
            </label>
          </Paper>
        </Grid>
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
      </Box>
    )
  }
}

export default App