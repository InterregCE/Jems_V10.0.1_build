import {Component} from '@angular/core';
import {CallPageSidenavService} from '../services/call-page-sidenav.service';
import {FormService} from '@common/components/section/form/form.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'jems-notifications-settings',
  templateUrl: './notifications-settings.component.html',
  styleUrls: ['./notifications-settings.component.scss'],
  providers: [FormService]
})
export class NotificationsSettingsComponent{

  constructor(private callSidenavService: CallPageSidenavService,
              private router: RoutingService,
              private activatedRoute: ActivatedRoute,) { }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }
}
