import {NutsImportService} from '@cat/api';
import {Injectable} from '@angular/core';
import {shareReplay, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../utils/log';
import {Observable} from 'rxjs';

@Injectable()
export class NutsStoreService {

    private nuts$ = this.nutsService.getNuts()
        .pipe(
            take(1),
            tap(nuts => Log.info('Fetched programme nuts', this, nuts)),
            shareReplay(1)
        );

    constructor(private nutsService: NutsImportService) {

    }

    getNuts(): Observable<any> {
        return this.nuts$;
    }

}