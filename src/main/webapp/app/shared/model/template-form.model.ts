export interface ITemplateForm {
  id?: number;
  templateFormID?: number | null;
  formType?: string | null;
}

export const defaultValue: Readonly<ITemplateForm> = {};
