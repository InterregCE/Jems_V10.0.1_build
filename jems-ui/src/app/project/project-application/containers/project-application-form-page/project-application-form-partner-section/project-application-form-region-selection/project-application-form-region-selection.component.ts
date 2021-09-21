import {ChangeDetectionStrategy, Component} from '@angular/core';
import {NutsStore} from '@common/services/nuts.store';
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
    this.partnerStore.partner$,
    this.nutsStore.getNuts()
  ])
    .pipe(
      map(([partner, nuts]) => ({partner, nuts}))
    );

  constructor(private nutsStore: NutsStore,
              public projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore) {
  }
}
