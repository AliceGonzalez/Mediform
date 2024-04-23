import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Login from './login';
import LoginDetail from './login-detail';
import LoginUpdate from './login-update';
import LoginDeleteDialog from './login-delete-dialog';

const LoginRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Login />} />
    <Route path="new" element={<LoginUpdate />} />
    <Route path=":id">
      <Route index element={<LoginDetail />} />
      <Route path="edit" element={<LoginUpdate />} />
      <Route path="delete" element={<LoginDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LoginRoutes;
