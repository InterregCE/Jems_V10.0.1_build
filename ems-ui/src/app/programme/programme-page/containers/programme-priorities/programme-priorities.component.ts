import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProgrammePriorityService} from '@cat/api'
import {tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {Permission} from '../../../../security/permissions/permission';
import {BaseComponent} from '@common/components/base-component';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';

@Component({
  selector: 'app-programme-priorities',
  templateUrl: './programme-priorities.component.html',
  styleUrls: ['./programme-priorities.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePrioritiesComponent extends BaseComponent {
  Permission = Permission;

  priorities$ = this.programmePriorityService.get(0, 100, 'code,asc')
    .pipe(
      tap(page => Log.info('Fetched the priorities:', this, page.content)),
    );

  constructor(private programmePriorityService: ProgrammePriorityService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
    this.programmePageSidenavService.init(this.destroyed$);
  }
}
