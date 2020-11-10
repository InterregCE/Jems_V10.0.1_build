import {
  ChangeDetectionStrategy,
  Component
} from '@angular/core';
import {NutsStoreService} from '../../../../../../common/services/nuts-store.service';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';

@Component({
  selector: 'app-project-application-form-region-selection',
  templateUrl: './project-application-form-region-selection.component.html',
  styleUrls: ['./project-application-form-region-selection.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormRegionSelectionComponent {

  details$ = combineLatest([
    this.partnerStore.getProjectPartner(),
    this.nutsStore.getNuts()
  ])
    .pipe(
      map(([partner, nuts]) => ({partner, nuts}))
    );

  constructor(private nutsStore: NutsStoreService,
              public projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore) {
  }
}
