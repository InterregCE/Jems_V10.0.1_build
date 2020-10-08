import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {Log} from '../utils/log';

@Injectable({providedIn: 'root'})
export class TabService {
  private currentTabs = new Map<string, ReplaySubject<number>>();

  currentTab(key: string): Observable<number> {
    const currentTab$ = this.getCurrentTab(key);
    const storedTab = localStorage.getItem(key);
    if (storedTab) {
      currentTab$.next(Number(storedTab));
    }
    return currentTab$.asObservable();
  }

  changeTab(key: string, newTab: number): void {
    this.getCurrentTab(key).next(newTab);
    Log.debug('Tab changed', key, newTab);
    localStorage.setItem(key, String(newTab));
  }

  cleanupTab(key: string): void {
    localStorage.removeItem(key);
    this.currentTabs.get(key)?.complete();
    this.currentTabs.delete(key);
  }

  private getCurrentTab(key: string): ReplaySubject<number> {
    const currentTab$ = this.currentTabs.get(key) || new ReplaySubject<number>();
    this.currentTabs.set(key, currentTab$);
    return currentTab$;
  }
}
