import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ChildVisits from './child-visits';
import ChildVisitsDetail from './child-visits-detail';
import ChildVisitsUpdate from './child-visits-update';
import ChildVisitsDeleteDialog from './child-visits-delete-dialog';

const ChildVisitsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ChildVisits />} />
    <Route path="new" element={<ChildVisitsUpdate />} />
    <Route path=":id">
      <Route index element={<ChildVisitsDetail />} />
      <Route path="edit" element={<ChildVisitsUpdate />} />
      <Route path="delete" element={<ChildVisitsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ChildVisitsRoutes;
