import React from "react";
import { screen } from '@testing-library/react';
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import ProcessDetails from "./ProcessDetails";

let container = null;
beforeEach(() => {
  // setup a DOM element as a render target
  container = document.createElement("div");
  document.body.appendChild(container);
});

afterEach(() => {
  // cleanup on exiting
  unmountComponentAtNode(container);
  container.remove();
  container = null;
});

it("renders process details", async () => {
  const fakeJobs = [{ "id": "52fda7c8-72c8-425a-a24c-8b915b5d1a38", "bulkProcess": "NEW_ADDRESS", "createdAt": "2021-05-24T11:01:33.635767+01:00", "lastUpdatedAt": "2021-05-24T11:02:38.364848+01:00", "fileName": "100_per_treatment_code.csv", "fileRowCount": 4701, "stagedRowCount": 4700, "processedRowCount": 4700, "rowErrorCount": 0, "jobStatus": "PROCESSED_OK", "fatalErrorDescription": null }];
  jest.spyOn(global, "fetch").mockImplementation(() =>
    Promise.resolve({
      json: () => Promise.resolve(fakeJobs)
    })
  );

  // Use the asynchronous version of act to apply resolved promises
  await act(async () => {
    render(<ProcessDetails bulkProcess={{ "bulkProcess": "NEW_ADDRESS", "title": "New Address" }} />, container);
  });

  const titleElement = screen.getByText(/New Address/i);
  expect(titleElement).toBeInTheDocument();

  const fileNameElement = screen.getByText(/100_per_treatment_code.csv/i);
  expect(fileNameElement).toBeInTheDocument();

  // remove the mock to ensure tests are completely isolated
  global.fetch.mockRestore();
});