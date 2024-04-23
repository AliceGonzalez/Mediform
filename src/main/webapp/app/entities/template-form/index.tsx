import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TemplateForm from './template-form';
import TemplateFormDetail from './template-form-detail';
import TemplateFormUpdate from './template-form-update';
import TemplateFormDeleteDialog from './template-form-delete-dialog';

const TemplateFormRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TemplateForm />} />
    <Route path="new" element={<TemplateFormUpdate />} />
    <Route path=":id">
      <Route index element={<TemplateFormDetail />} />
      <Route path="edit" element={<TemplateFormUpdate />} />
      <Route path="delete" element={<TemplateFormDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TemplateFormRoutes;
