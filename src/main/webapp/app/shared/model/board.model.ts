import { IItem } from 'app/shared/model//item.model';

export interface IBoard {
  id?: number;
  title?: string;
  description?: string;
  items?: IItem[];
}

export const defaultValue: Readonly<IBoard> = {};
