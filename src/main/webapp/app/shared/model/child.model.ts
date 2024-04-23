import dayjs from 'dayjs';
import { IParent } from 'app/shared/model/parent.model';

export interface IChild {
  id?: number;
  childID?: number | null;
  name?: string | null;
  lastName?: string | null;
  dob?: dayjs.Dayjs | null;
  parent?: IParent | null;
}

export const defaultValue: Readonly<IChild> = {};
