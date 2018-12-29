import { IBoard } from 'app/shared/model//board.model';

export const enum Type {
  TASK = 'TASK',
  REMINDER = 'REMINDER',
  BIRTHDAY = 'BIRTHDAY'
}

export interface IItem {
  id?: number;
  type?: Type;
  title?: string;
  description?: string;
  board?: IBoard;
}

export const defaultValue: Readonly<IItem> = {};
