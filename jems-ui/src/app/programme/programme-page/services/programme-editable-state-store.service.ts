import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProgrammeDataService} from '@cat/api';

@Injectable()
export class ProgrammeEditableStateStore {
    isProgrammeEditableDependingOnCall$ = new ReplaySubject<boolean>(1);

    constructor(private programmeDataService: ProgrammeDataService) {
    }

    init(): void {
        this.isProgrammeEditable();
    }

    private isProgrammeEditable(): void {
        this.programmeDataService.isProgrammeSetupLocked()
            .pipe(
                tap(flag => Log.info('Fetched programme is locked:', flag)),
                tap(flag => this.isProgrammeEditableDependingOnCall$.next(flag))
            ).subscribe();
    }
}
