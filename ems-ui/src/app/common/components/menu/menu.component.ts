import {Component, Input, OnInit} from '@angular/core';
import {MenuItemConfiguration} from './model/menu-item.configuration';
import {Router} from '@angular/router';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent extends BaseComponent implements OnInit {
  @Input()
  items: MenuItemConfiguration[];

  constructor(public router: Router) {
    super();
  }

  ngOnInit(): void {
  }

  callAction(item: MenuItemConfiguration): void {
    item.action(item.isInternal, item.route);
  }
}
