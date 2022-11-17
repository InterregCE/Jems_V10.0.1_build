import {Injectable} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProgrammeDataService, ProjectStatusDTO, UserRoleCreateDTO} from '@cat/api';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable({providedIn: 'root'})
export class ProgrammeEditableStateStore {
  isProgrammeEditableDependingOnCall$: Observable<boolean>;
  isFastTrackEditableDependingOnReports$: Observable<boolean>;
  hasOnlyViewPermission$: Observable<boolean>;
  hasEditPermission$: Observable<boolean>;
  hasContractedProjects$: Observable<boolean>;

  firstCallPublished$ = new Subject<void>();
  firstReportCreated$ = new Subject<void>();
  firstContractedProject$ = new Subject<void>();

  constructor(
    private programmeDataService: ProgrammeDataService,
    private permissionService: PermissionService,
  ) {
    this.isProgrammeEditableDependingOnCall$ = this.isProgrammeEditable();
    this.isFastTrackEditableDependingOnReports$ = this.isAnyReportCreated();
    this.hasOnlyViewPermission$ = this.hasUserOnlyViewPermission();
    this.hasEditPermission$ = this.hasUserEditPermission();
    this.hasContractedProjects$ = this.programmeHasContractedProjects();
  }

  private isAnyReportCreated(): Observable<boolean> {
    return this.firstReportCreated$
      .pipe(
        startWith(null),
        switchMap(() => this.programmeDataService.isAnyReportCreated()),
        tap(flag => Log.info('Lump Sum fast track is locked because of reporting:', flag)),
        shareReplay(1),
      );
  }

  isFastTrackLumpSumReadyForPayment(programmeLumpSumId: number | null | undefined): Observable<boolean> {
    if (programmeLumpSumId) {
      return this.programmeDataService.isFastTrackLumpSumReadyForPayment(programmeLumpSumId)
        .pipe(
          tap(flag => Log.info('Lump Sum fast track is locked because of contracting:', flag)),
          shareReplay(1),
        );
    }
    return of(false);
  }

  private isProgrammeEditable(): Observable<boolean> {
    return this.firstCallPublished$
      .pipe(
        startWith(null),
        switchMap(() => this.programmeDataService.isProgrammeSetupLocked()),
        tap(flag => Log.info('Fetched programme is locked:', flag)),
        shareReplay(1),
      );
  }

  private hasUserOnlyViewPermission(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.ProgrammeSetupUpdate)
      .pipe(
        map(hasUpdate => !hasUpdate),
        shareReplay(1),
      );
  }

  private hasUserEditPermission(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.ProgrammeSetupUpdate)
      .pipe(
        shareReplay(1),
      );
  }

  private programmeHasContractedProjects(): Observable<boolean>{
      return this.firstContractedProject$.pipe(
        startWith(null),
        switchMap(() => this.programmeDataService.hasProjectsInStatus(ProjectStatusDTO.StatusEnum.CONTRACTED)),
        tap(flag => Log.info('Fetched programme has contracted projects: ', flag)),
      );
  }

 }
