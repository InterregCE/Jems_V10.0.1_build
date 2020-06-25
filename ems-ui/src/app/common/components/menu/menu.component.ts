import {Component, Input} from '@angular/core';
import {MenuItemConfiguration} from './model/menu-item.configuration';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent {
  @Input()
  items: MenuItemConfiguration[];

  activeLink: MenuItemConfiguration | undefined;

  callAction(item: MenuItemConfiguration): void {
    if (item.isInternal) {
      this.activeLink = item;
    }
    item.action(item.isInternal, item.route);
  }

  isActive(item: MenuItemConfiguration): boolean {
    if (this.activeLink) {
      return this.activeLink === item;
    }
    return this.items && item === this.items[0];
  }
}
