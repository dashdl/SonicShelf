export const BASE_URL = 'http://localhost:8080';
// export const BASE_URL = 'http://192.168.111.131:8080';

export const getFullUrl = (path) => {
  return `${BASE_URL}${path}`;
};
