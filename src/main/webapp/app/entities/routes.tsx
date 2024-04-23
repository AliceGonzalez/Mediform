import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Login from './login';
import Parent from './parent';
import Child from './child';
import ChildVisits from './child-visits';
import TemplateForm from './template-form';
import SavedForms from './saved-forms';
import ChildData from './child-data';
import FormStatus from './form-status';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="login/*" element={<Login />} />
        <Route path="parent/*" element={<Parent />} />
        <Route path="child/*" element={<Child />} />
        <Route path="child-visits/*" element={<ChildVisits />} />
        <Route path="template-form/*" element={<TemplateForm />} />
        <Route path="saved-forms/*" element={<SavedForms />} />
        <Route path="child-data/*" element={<ChildData />} />
        <Route path="form-status/*" element={<FormStatus />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
