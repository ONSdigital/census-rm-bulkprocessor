export const convertStatusText = (status) => {
  if (status === 'FILE_UPLOADED') {
    return 'File Uploaded'
  } else if (status === 'STAGING_IN_PROGRESS') {
    return 'Staging in Progress'
  } else if (status === 'PROCESSING_IN_PROGRESS') {
    return 'Processing in Progress'
  } else if (status === 'PROCESSED_OK') {
    return 'Processed OK'
  } else if (status === 'PROCESSED_WITH_ERRORS') {
    return 'Processed With Errors'
  } else if (status === 'PROCESSED_TOTAL_FAILURE') {
    return 'Processing Failed'
  }

  return 'Unknown Status';
};