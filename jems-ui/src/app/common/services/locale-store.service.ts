import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class LocaleStore {
  /**
   * System locale identified by the browser.
   */
  public static browserLocale(): string {
    return navigator.language;
  }

  /**
   * Customizable later.
   * User configured locale.
   */
  public userLocale(): string {
    return 'de-DE';
  }
}
