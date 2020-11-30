import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';
import {WorkPackageService} from '@cat/api';
import {ProjectWorkpackageStoreService} from '../../services/project-workpackage-store.service';

@Component({
  selector: 'app-project-application-form-work-package-objectives',
  templateUrl: './project-application-form-work-package-objectives.component.html',
  styleUrls: ['./project-application-form-work-package-objectives.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackageObjectivesComponent extends BaseComponent implements OnInit {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public workPackageStore: ProjectWorkpackageStoreService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: Router) {
    super();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);

    if (this.workPackageId) {
      return;
    }

    this.workPackageStore.init(null, this.projectId);
  }
}
