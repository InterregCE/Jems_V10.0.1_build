import {MenuItemConfiguration} from './menu-item.configuration';

export class MenuConfiguration {
  // items that are part of te menu.
  items: MenuItemConfiguration[];

  public constructor(init?: Partial<MenuConfiguration>) {
    Object.assign(this, init);
  }
}
