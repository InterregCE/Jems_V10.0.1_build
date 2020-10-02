import {Component, Input} from '@angular/core';
import {MenuItemConfiguration} from './model/menu-item.configuration';
import {Router} from '@angular/router';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent {

  @Input()
  items: MenuItemConfiguration[];

  constructor(public router: Router) {
  }

}
