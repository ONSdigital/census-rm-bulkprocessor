import React, { Component } from 'react';
import '@fontsource/roboto';
import {
  Button,
  Grid,
} from '@material-ui/core';

class ChooseProcess extends Component {
  constructor(props) {
    super(props)

    this.state = {
      processors: []
    }
  }

  componentDidMount() {
    fetch("/bulkprocess")
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            processors: result
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

  render() {
    const buttonFragment = this.state.processors.map((process) => (
      <Grid key={process['bulkProcess']} item xs={12} sm={4}>
        <Button variant="contained" fullWidth={true} onClick={() => this.props.onChooseProcess(process)}>
          {process['title']}
        </Button>
      </Grid>
    ))


    return (
      <div style={{ padding: 10 }} >
        <Grid container spacing={1} direction="row" alignItems="center">
          {buttonFragment}
        </Grid>
      </div>
    )
  }
}

export default ChooseProcess