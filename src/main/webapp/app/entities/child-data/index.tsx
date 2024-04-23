import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ChildData from './child-data';
import ChildDataDetail from './child-data-detail';
import ChildDataUpdate from './child-data-update';
import ChildDataDeleteDialog from './child-data-delete-dialog';

const ChildDataRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ChildData />} />
    <Route path="new" element={<ChildDataUpdate />} />
    <Route path=":id">
      <Route index element={<ChildDataDetail />} />
      <Route path="edit" element={<ChildDataUpdate />} />
      <Route path="delete" element={<ChildDataDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ChildDataRoutes;
