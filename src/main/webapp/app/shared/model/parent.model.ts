export interface IParent {
  id?: number;
  parentID?: number | null;
  name?: string | null;
  lastName?: string | null;
}

export const defaultValue: Readonly<IParent> = {};
