import { IParent } from 'app/shared/model/parent.model';

export interface ILogin {
  id?: number;
  username?: string | null;
  password?: string | null;
  parentID?: IParent | null;
}

export const defaultValue: Readonly<ILogin> = {};
