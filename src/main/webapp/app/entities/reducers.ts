import login from 'app/entities/login/login.reducer';
import parent from 'app/entities/parent/parent.reducer';
import child from 'app/entities/child/child.reducer';
import childVisits from 'app/entities/child-visits/child-visits.reducer';
import templateForm from 'app/entities/template-form/template-form.reducer';
import savedForms from 'app/entities/saved-forms/saved-forms.reducer';
import childData from 'app/entities/child-data/child-data.reducer';
import formStatus from 'app/entities/form-status/form-status.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  login,
  parent,
  child,
  childVisits,
  templateForm,
  savedForms,
  childData,
  formStatus,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
