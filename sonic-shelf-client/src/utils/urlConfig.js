export const BASE_URL = 'http://localhost:8080';

export const getFullUrl = (path) => {
  return `${BASE_URL}${path}`;
};
