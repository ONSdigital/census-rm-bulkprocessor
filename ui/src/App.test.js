import { render, screen } from '@testing-library/react';
import App from './App';

test('renders toolbar title', () => {
  render(<App />);
  const titleElement = screen.getByText(/RM Bulk Processing/i);
  expect(titleElement).toBeInTheDocument();
});
