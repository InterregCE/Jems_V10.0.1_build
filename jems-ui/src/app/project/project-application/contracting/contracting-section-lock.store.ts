import {Injectable} from '@angular/core';
import {ContractingSectionLockService, ProjectContractingPartnerLockService} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map, shareReplay, switchMap, take, tap} from 'rxjs/operators';
import {
    ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ContractingSection} from '@project/project-application/contracting/contracting-section';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {ContractingStore} from '@project/project-application/contracting/contracting.store';

@Injectable({
    providedIn: 'root'
})
@UntilDestroy()
export class ContractingSectionLockStore {

    lockedSections$ = new Observable<string[]>();
    refreshLockedSections$ = new BehaviorSubject<any>(null);
    projectId: number;

    constructor(
        private contractingSectionLockService: ContractingSectionLockService,
        private contractingPartnerLockService: ProjectContractingPartnerLockService,
        private projectStore: ProjectStore,
        private contractPartnerStore: ContractPartnerStore,
        private contractingStore: ContractingStore,
    ) {
        this.lockedSections$ = combineLatest([
            this.projectStore.projectId$,
            this.refreshLockedSections$
        ]).pipe(
            tap(([projectId, refreshLockedSections]) => this.projectId = projectId),
            switchMap(([projectId, refreshLockedSections]) => this.getLockedSections(projectId))
        );
    }

    getLockedSections(projectId: number): Observable<string[]> {
        return this.contractingSectionLockService.getLockedSections(projectId).pipe(
            shareReplay(1)
        );
    }

    lockSection(contractingSection: ContractingSection): Observable<any> {
        return this.contractingSectionLockService.lock(this.projectId, contractingSection.toString()).pipe(
            map( () => this.refreshLockedSections$.next(null) ),
        );
    }

    unlockSection(contractingSection: ContractingSection): Observable<any> {
        return this.contractingSectionLockService.unlock(this.projectId, contractingSection.toString()).pipe(
            map( () => this.refreshLockedSections$.next(null) )
        );
    }

    lockPartner(): Observable<any> {
    return combineLatest([
      this.contractPartnerStore.partnerId$,
      this.projectStore.projectId$,
    ]).pipe(
        take(1),
        switchMap(([partnerId, projectId]) => this.contractingPartnerLockService.lock(Number(partnerId), projectId)),
        tap(() => this.contractingStore.refreshPartners()),
        untilDestroyed(this)
    );
  }

  unlockPartner(): Observable<any> {
    return combineLatest([
      this.contractPartnerStore.partnerId$,
      this.projectStore.projectId$,
    ]).pipe(
        take(1),
        switchMap(([partnerId, projectId]) =>  this.contractingPartnerLockService.unlock(Number(partnerId), projectId)),
        tap(() => this.contractingStore.refreshPartners()),
        untilDestroyed(this)
    );
  }
}
