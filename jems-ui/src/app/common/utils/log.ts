import {isDevMode} from '@angular/core';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';

export class Log {

  static error(message: any, ...optionalParams: any[]): void {
    /* eslint no-console: "off" */
    console.error(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  static debug(message: any, ...optionalParams: any[]): void {
    if (!isDevMode()) {
      return;
    }
    /* eslint no-console: "off" */
    console.debug(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  static info(message: any, ...optionalParams: any[]): void {
    /* eslint no-console: "off" */
    console.log(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  static warn(message: any, ...optionalParams: any[]): void {
    /* eslint no-console: "off" */
    console.warn(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  private static getMessage(message: any, optionalParams: any[]): string {
    const location: string = optionalParams && optionalParams[0]?.constructor?.name;
    if (location) {
      return `${new Date().toLocaleTimeString()} - ${location}: ${message}`;
    }
    return `${new Date().toLocaleTimeString()}: ${message}`;
  }

  static debugOperator = (level: number, message: string) =>
    (source: Observable<any>) => source
      .pipe(
        tap(val => {
          if (level >= rxjsLoggingLevel) {
            console.log(message + ': ', val);
          }
        })
      );
}

export function setRxJsLoggingLevel(level: RxJsLoggingLevel) {
  rxjsLoggingLevel = level;
}

export enum RxJsLoggingLevel {
  TRACE,
  DEBUG,
  INFO,
  ERROR
}

let rxjsLoggingLevel = RxJsLoggingLevel.INFO;
