import { IChild } from 'app/shared/model/child.model';
import { ITemplateForm } from 'app/shared/model/template-form.model';

export interface ISavedForms {
  id?: number;
  savedFormID?: number | null;
  formID?: number | null;
  formType?: string | null;
  child?: IChild | null;
  templateForm?: ITemplateForm | null;
}

export const defaultValue: Readonly<ISavedForms> = {};
