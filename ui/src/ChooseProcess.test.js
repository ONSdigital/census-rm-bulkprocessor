import React from "react";
import { screen } from '@testing-library/react';
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import ChooseProcess from "./ChooseProcess";

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

it("renders buttons", async () => {
  const fakeProcesses = [{"bulkProcess":"NEW_ADDRESS","title":"New Address"},{"bulkProcess":"REFUSAL","title":"Refusal"}];
  jest.spyOn(global, "fetch").mockImplementation(() =>
    Promise.resolve({
      json: () => Promise.resolve(fakeProcesses)
    })
  );

  // Use the asynchronous version of act to apply resolved promises
  await act(async () => {
    render(<ChooseProcess/>, container);
  });

  const newAddressButton = screen.getByText(/New Address/i);
  expect(newAddressButton).toBeInTheDocument();
  const refusalButton = screen.getByText(/Refusal/i);
  expect(refusalButton).toBeInTheDocument();

  // remove the mock to ensure tests are completely isolated
  global.fetch.mockRestore();
});