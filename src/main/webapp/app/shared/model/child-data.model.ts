import dayjs from 'dayjs';
import { IChild } from 'app/shared/model/child.model';

export interface IChildData {
  id?: number;
  childDataID?: number | null;
  name?: string | null;
  lastName?: string | null;
  dob?: dayjs.Dayjs | null;
  child?: IChild | null;
}

export const defaultValue: Readonly<IChildData> = {};
