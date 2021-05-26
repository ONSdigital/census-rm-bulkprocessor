import { render, screen } from '@testing-library/react';
import JobDetails from './JobDetails';

test('renders job details', () => {
  const dummyJob = { "id": "ce57aea6-83e8-4772-b4d0-eb4437411737", "bulkProcess": "NEW_ADDRESS", "createdAt": "2021-05-25T08:42:18.704338+01:00", "createdBy": "billy@silly.com", "lastUpdatedAt": "2021-05-25T08:53:26.667158+01:00", "fileName": "1000_per_treatment_code.csv", "fileRowCount": 46057, "stagedRowCount": 46056, "processedRowCount": 46056, "rowErrorCount": 0, "jobStatus": "PROCESSED_OK", "fatalErrorDescription": null }
  render(<JobDetails job={dummyJob} showDetails={true} />);

  const fileElement = screen.getByText(/File: 1000_per_treatment_code.csv/i);
  expect(fileElement).toBeInTheDocument();

  const fileLineCountElement = screen.getByText(/File line count: 46057/i);
  expect(fileLineCountElement).toBeInTheDocument();

  const jobStatusElement = screen.getByText(/Job status: Processed OK/i);
  expect(jobStatusElement).toBeInTheDocument();

  const rowsStagedElement = screen.getByText(/Rows staged:/i);
  expect(rowsStagedElement).toBeInTheDocument();

  const rowsProcessedElement = screen.getByText(/Rows processed:/i);
  expect(rowsProcessedElement).toBeInTheDocument();

  const errorsElement = screen.getByText(/Errors: 0/i);
  expect(errorsElement).toBeInTheDocument();

  const uploadedTimestampElement = screen.getByText(/Uploaded at: 2021-05-25T08:42:18/i);
  expect(uploadedTimestampElement).toBeInTheDocument();

  const uploadedByElement = screen.getByText(/Uploaded by: billy@silly.com/i);
  expect(uploadedByElement).toBeInTheDocument();
});
