import {ChangeDetectionStrategy, Component} from '@angular/core';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {ProjectApplicationFormSidenavService} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-privileges-page',
  templateUrl: './privileges-page.component.html',
  styleUrls: ['./privileges-page.component.scss'],
  providers: [PrivilegesPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PrivilegesPageComponent {
  Alert = Alert;

  constructor(public pageStore: PrivilegesPageStore,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
  }
}
