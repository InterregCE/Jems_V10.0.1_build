import {Component, Input} from '@angular/core';
import {MenuItemConfiguration} from './model/menu-item.configuration';
import {NavigationEnd, Router} from '@angular/router';
import {BaseComponent} from '@common/components/base-component';
import {filter, takeUntil} from 'rxjs/operators';
import {Log} from '../../utils/log';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent extends BaseComponent {
  @Input()
  items: MenuItemConfiguration[];

  activeLink: MenuItemConfiguration | undefined;

  constructor(private router: Router) {
    super();
    router.events
      .pipe(
        takeUntil(this.destroyed$),
        filter(val => val instanceof NavigationEnd)
      )
      .subscribe((val: NavigationEnd) => {
        const activeItem = this.items.find(item => item.route === val.url);
        if (activeItem) {
          Log.debug('Switched bar menu item', this, activeItem.route);
          this.activeLink = activeItem;
        }
      });
  }

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
