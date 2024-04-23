import { ITemplateForm } from 'app/shared/model/template-form.model';
import { IChild } from 'app/shared/model/child.model';

export interface IFormStatus {
  id?: number;
  formStatusID?: number | null;
  status?: string | null;
  templateForm?: ITemplateForm | null;
  child?: IChild | null;
}

export const defaultValue: Readonly<IFormStatus> = {};
