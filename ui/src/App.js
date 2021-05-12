import React, { Component } from 'react';
import '@fontsource/roboto';
import axios from 'axios';
import {
  Button,
  Box,
  Grid,
  Paper,
  AppBar,
  Toolbar,
  Typography,
  LinearProgress,
  Snackbar,
  SnackbarContent,
  Dialog,
  DialogContent,
  makeStyles
} from '@material-ui/core';

class App extends Component {
  constructor(props) {
    super(props)

    // The state holds any values which should initiate a re-rendering of the UI, if they change
    this.state = {
      fileProgress: 0,  // Percentage of the file uploaded
      fileUploadSuccess: false, // Flag to flash the snackbar message on the screen, when file uploads successfully
      uploadInProgress: false, // Flag to display the file upload progress modal dialog
      processors: []
    }
  }
  componentDidMount() {
      fetch("/processors")
        .then(res => res.json())
        .then(
          (result) => {
            this.setState({
            processors: result['processors']
            });
          },
          // Note: it's important to handle errors here
          // instead of a catch() block so that we don't swallow
          // exceptions from actual bugs in components.
          (error) => {
            this.setState({
              isLoaded: true,
              error
            });
          }
        )
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
  processorSelected = (e) => {
    alert()
  }

  render() {
    const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  paper: {
    padding: theme.spacing(1),
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
}));
    return (
      <Box>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" color="inherit">
              RM Bulk Processing
            </Typography>
          </Toolbar>
        </AppBar>
        <Grid
  container spacing={1}
  direction="row"
  alignItems="center"
>
          {/*<Paper elevation={3} style={{ margin: 10, padding: 10 }}>*/}
          {/*  <Typography variant="h8" color="inherit" style={{ margin: 10, padding: 10 }}>*/}
          {/*    Please upload a bulk file for processing*/}
          {/*  </Typography>*/}
          {/*  <input*/}
          {/*    accept="csv/*"*/}
          {/*    style={{ display: 'none' }}*/}
          {/*    id="contained-button-file"*/}
          {/*    type="file"*/}
          {/*    onChange={(e) => {*/}
          {/*      this.handleUpload(e)*/}
          {/*    }}*/}
          {/*  />*/}
          {/*  <label htmlFor="contained-button-file">*/}
          {/*    <Button variant="contained" component="span">*/}
          {/*      Upload*/}
          {/*    </Button>*/}
          {/*  </label>*/}
          {/*</Paper>*/}
          {this.state.processors.map((process) =>(
              <Grid item xs={12} sm={4}> <Button onClick={() => alert(process)}><Box bgcolor="text.secondary" p={2}>{process}</Box></Button></Grid>
          ))}
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