import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';

@Component({
  selector: 'app-project-application-partner-identity',
  templateUrl: './project-application-partner-identity.component.html',
  styleUrls: ['./project-application-partner-identity.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerIdentityComponent {

  constructor(public partnerStore: ProjectPartnerStore,
              public projectStore: ProjectStore) {
  }
}
