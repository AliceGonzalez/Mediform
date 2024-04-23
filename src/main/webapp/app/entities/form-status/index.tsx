import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FormStatus from './form-status';
import FormStatusDetail from './form-status-detail';
import FormStatusUpdate from './form-status-update';
import FormStatusDeleteDialog from './form-status-delete-dialog';

const FormStatusRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FormStatus />} />
    <Route path="new" element={<FormStatusUpdate />} />
    <Route path=":id">
      <Route index element={<FormStatusDetail />} />
      <Route path="edit" element={<FormStatusUpdate />} />
      <Route path="delete" element={<FormStatusDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FormStatusRoutes;
