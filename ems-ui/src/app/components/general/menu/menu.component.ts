import {Component, Input, OnInit} from '@angular/core';
import {MenuConfiguration} from '../configurations/menu.configuration';
import {MenuItemConfiguration} from '../configurations/menu-item.configuration';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  @Input()
  configuration: MenuConfiguration;

  activeLink: MenuItemConfiguration;

  ngOnInit(): void {
    this.activeLink = this.configuration.items[0];
  }

  callAction(item: MenuItemConfiguration): void {
    if (item.isInternal) {
      this.activeLink = item;
    }
    item.action(item.isInternal, item.route);
  }
}
