import dayjs from 'dayjs';
import { IChild } from 'app/shared/model/child.model';

export interface IChildVisits {
  id?: number;
  visitID?: number | null;
  visitType?: string | null;
  visitDate?: dayjs.Dayjs | null;
  child?: IChild | null;
}

export const defaultValue: Readonly<IChildVisits> = {};
