import {AfterViewInit, ChangeDetectionStrategy, Component, Input, TemplateRef, ViewChild} from '@angular/core';
import {ProjectApplicationFormSidenavService} from '../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {combineLatest, Observable} from 'rxjs';
import {ProjectStatusDTO, ProjectUserDTO, ProjectVersionDTO, UserRoleDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectPageTemplateStore} from './project-page-template-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import ProjectStatusEnum = ProjectUserDTO.ProjectStatusEnum;
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';

@UntilDestroy()
@Component({
  selector: 'jems-project-page-template',
  templateUrl: './project-page-template.component.html',
  styleUrls: ['./project-page-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPageTemplateComponent implements AfterViewInit {
  Alert = Alert;
  PermissionsEnum = PermissionsEnum;
  ProjectStatusEnum = ProjectStatusEnum;

  @ViewChild('sidenavVersionSelect', {static: true})
  sidenavVersionSelect: TemplateRef<any>;

  @Input() needsCard = false;
  @Input() isVersionedData = true;

  @Input() titleText: string;
  @Input() titleKey: string;

  @Input() subTitleText: string;
  @Input() subTitleKey: string;

  @Input() descriptionText: string;
  @Input() descriptionKey: string;

  versionSelectData$: Observable<{
    versions: {
      currentVersion: ProjectVersionDTO;
      lastApprovedVersion: ProjectVersionDTO;
      pastVersions: ProjectVersionDTO[];
    };
    selectedVersion: ProjectVersionDTO | undefined;
    versionWarnData: {
      selected: ProjectVersionDTO | undefined;
      current: ProjectVersionDTO | undefined;
      selectedIsCurrent: boolean;
      versions: ProjectVersionDTO[];
    };
    isSelectedSameAsLastApproved: boolean;
    stringForSelectedVersion: string;
  }>;

  constructor(
    public projectSidenavService: ProjectApplicationFormSidenavService,
    public pageStore: ProjectPageTemplateStore,
    private localelDatePipe: LocaleDatePipe,
    private customTranslatePipe: CustomTranslatePipe,
  ) {
    this.versionSelectData$ = combineLatest([
      this.pageStore.selectedVersion$,
      this.pageStore.currentVersion$,
      this.pageStore.isSelectedVersionCurrent$,
      this.pageStore.versions$,
      this.versions(),
      this.pageStore.projectStatus$,
    ]).pipe(
      map(([selectedVersion, currentVersion, selectedIsCurrent, versions, versions2, projectStatus]) => ({
        versions: versions2,
        selectedVersion,
        versionWarnData: {
          selected: selectedVersion,
          current: currentVersion,
          selectedIsCurrent,
          versions,
        },
        isSelectedSameAsLastApproved: this.isSelectedSameAsLastApproved(selectedVersion),
        stringForSelectedVersion: selectedVersion ? this.getStatusStringForSelectedVersion(projectStatus, selectedVersion) : '',
      })),
    );
  }

  ngAfterViewInit(): void {
    this.pageStore.versions$.pipe(untilDestroyed(this)).subscribe(() =>
      this.projectSidenavService.versionSelectTemplate$.next(this.sidenavVersionSelect)
    );
  }

  versions(): Observable<any> {
    return this.pageStore.versions$
      .pipe(
        map(versions => ({
            currentVersion: versions.find(version => version.current),
            lastApprovedVersion: versions.find(version => this.isStatusApprovedOrContracted(version)),
            pastVersions: versions.filter(version => !version.current &&
              (version !== versions.find((approvedVersion) => this.isStatusApprovedOrContracted(approvedVersion))))
          }
        ))
      );
  }

  isStatusApprovedOrContracted(currentVersion: ProjectVersionDTO): boolean {
    return currentVersion.status === ProjectStatusDTO.StatusEnum.APPROVED || currentVersion.status === ProjectStatusDTO.StatusEnum.CONTRACTED;
  }

  private isSelectedSameAsLastApproved(selectedVersion: ProjectVersionDTO | undefined): boolean {
    let isLastApproved = false;
    this.versions().subscribe( versions => {
      isLastApproved = versions.lastApprovedVersion === selectedVersion;
    });
    return isLastApproved;
  }

  noDecisionTaken(currentVersion: ProjectVersionDTO): boolean {
    return currentVersion.status !== ProjectStatusEnum.MODIFICATIONREJECTED && currentVersion.status !== ProjectStatusEnum.APPROVED && currentVersion.status !== ProjectStatusEnum.CONTRACTED;
  }

  getIconForProjectStatus(status: ProjectStatusEnum): String {
    switch (status) {
      case ProjectStatusEnum.MODIFICATIONREJECTED:
      case ProjectStatusEnum.INELIGIBLE:
      case ProjectStatusEnum.STEP1INELIGIBLE:
      case ProjectStatusEnum.NOTAPPROVED:
      case ProjectStatusEnum.STEP1NOTAPPROVED:
        return 'close';
      case ProjectStatusEnum.DRAFT:
      case ProjectStatusEnum.STEP1DRAFT:
      case ProjectStatusEnum.INMODIFICATION:
      case ProjectStatusEnum.MODIFICATIONSUBMITTED:
      case ProjectStatusEnum.MODIFICATIONPRECONTRACTING:
      case ProjectStatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED:
      case ProjectStatusEnum.RETURNEDTOAPPLICANT:
      case ProjectStatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS:
      case ProjectStatusEnum.SUBMITTED:
      case ProjectStatusEnum.STEP1SUBMITTED:
      case ProjectStatusEnum.CONDITIONSSUBMITTED:
        return 'edit';
      default:
        return 'done';
    }
  }

  isInModification(): boolean {
    let currentlyInModification = false;
    this.versionSelectData$.subscribe( version => {
      switch (version.selectedVersion?.status) {
        case ProjectStatusEnum.INMODIFICATION:
        case ProjectStatusEnum.MODIFICATIONSUBMITTED:
        case ProjectStatusEnum.MODIFICATIONPRECONTRACTING:
        case ProjectStatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED:
          currentlyInModification = true;
          break;
        default:
          break;
      }
    });
    return currentlyInModification;
  }

  private getStatusStringForSelectedVersion(projectStatus: ProjectStatusDTO, selectedVersion: ProjectVersionDTO): string {
    let date = '';
    let statusString: string = this.customTranslatePipe.transform('common.label.projectapplicationstatus.'+selectedVersion.status);

    switch (selectedVersion.status) {
      case ProjectStatusEnum.APPROVED:
      case ProjectStatusEnum.MODIFICATIONREJECTED:
      case ProjectStatusEnum.CONTRACTED:
        if (projectStatus.entryIntoForceDate) { // project was in modification
          date = this.localelDatePipe.transform(projectStatus.entryIntoForceDate, 'L');
          statusString += ', ' + this.customTranslatePipe.transform('project.versions.warn.modification');
        }
        else if (projectStatus.decisionDate) {  // project has been initially set to approved
          date = this.localelDatePipe.transform(projectStatus.decisionDate, 'L');
          statusString += ', ' + this.customTranslatePipe.transform('project.versions.warn.decision');
        }
        else {  // project has been initially set to contracted
          date = this.localelDatePipe.transform(selectedVersion.createdAt, 'L');
        }
        break;
      case ProjectStatusEnum.SUBMITTED:
      case ProjectStatusEnum.STEP1SUBMITTED:
      case ProjectStatusEnum.CONDITIONSSUBMITTED:
      case ProjectStatusEnum.MODIFICATIONSUBMITTED:
      case ProjectStatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED:
        date = this.localelDatePipe.transform(selectedVersion.createdAt, 'L');
        break;
      case ProjectStatusEnum.APPROVEDWITHCONDITIONS:
      case ProjectStatusEnum.NOTAPPROVED:
      case ProjectStatusEnum.INELIGIBLE:
      case ProjectStatusEnum.ELIGIBLE:
      case ProjectStatusEnum.STEP1APPROVED:
      case ProjectStatusEnum.STEP1APPROVEDWITHCONDITIONS:
      case ProjectStatusEnum.STEP1NOTAPPROVED:
      case ProjectStatusEnum.STEP1INELIGIBLE:
      case ProjectStatusEnum.STEP1ELIGIBLE:
        date = this.localelDatePipe.transform(projectStatus.decisionDate, 'L');
        statusString += ', ' + this.customTranslatePipe.transform('project.versions.warn.decision');
        break;
      default: // show no date on editable states or while waiting for a decision
        break;
    }

    return statusString + ' ' + date;
  }

}
