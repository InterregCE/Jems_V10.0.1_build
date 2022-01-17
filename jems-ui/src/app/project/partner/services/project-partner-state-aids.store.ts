import {Injectable} from '@angular/core';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectPartnerService, ProjectPartnerStateAidDTO} from '@cat/api';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {filter, shareReplay, switchMap, tap} from 'rxjs/operators';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectPartnerStateAidsStore {
  stateAid$: Observable<ProjectPartnerStateAidDTO>;

  private updatedStateAid$ = new Subject<ProjectPartnerStateAidDTO>();

  constructor(private partnerStore: ProjectPartnerStore,
              private projectPartnerService: ProjectPartnerService,
              private projectVersionStore: ProjectVersionStore) {
    this.stateAid$ = this.stateAid();
  }

  updateStateAid(partnerId: number, stateAid: ProjectPartnerStateAidDTO): Observable<ProjectPartnerStateAidDTO> {
    return this.projectPartnerService.updateProjectPartnerStateAid(partnerId, stateAid)
      .pipe(
        tap(saved => this.updatedStateAid$.next(saved)),
        tap(saved => Log.info('Updated the partner state aid', this, saved))
      );
  }

  private stateAid(): Observable<ProjectPartnerStateAidDTO> {
    const initialStateAid$ = combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$
    ])
      .pipe(
        filter(([partner]) => !!partner.id),
        switchMap(([partner, version]) => this.projectPartnerService.getProjectPartnerStateAid(partner.id, version)),
        tap(stateAid => Log.info('Fetched the partner state aid', this, stateAid)),
      );

    return merge(initialStateAid$, this.updatedStateAid$).pipe(shareReplay(1));
  }
}
