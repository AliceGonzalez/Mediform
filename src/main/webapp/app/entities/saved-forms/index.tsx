import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SavedForms from './saved-forms';
import SavedFormsDetail from './saved-forms-detail';
import SavedFormsUpdate from './saved-forms-update';
import SavedFormsDeleteDialog from './saved-forms-delete-dialog';

const SavedFormsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SavedForms />} />
    <Route path="new" element={<SavedFormsUpdate />} />
    <Route path=":id">
      <Route index element={<SavedFormsDetail />} />
      <Route path="edit" element={<SavedFormsUpdate />} />
      <Route path="delete" element={<SavedFormsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SavedFormsRoutes;
