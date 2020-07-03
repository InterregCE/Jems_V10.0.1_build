import { isDevMode } from '@angular/core';

export class Log {

  static error(message: any, ...optionalParams: any[]): void {
    console.error(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  static debug(message: any, ...optionalParams: any[]): void {
    if(!isDevMode()) {
      return;
    }
    console.debug(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  static info(message: any, ...optionalParams: any[]): void {
    console.log(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  static warn(message: any, ...optionalParams: any[]): void {
    console.warn(
      Log.getMessage(message, optionalParams),
      optionalParams
    );
  }

  private static getMessage(message: any, optionalParams: any[]): string {
    const location:string = optionalParams && optionalParams[0]?.constructor?.name;
    if (location) {
      return `${new Date().toLocaleTimeString()} - ${location}: ${message}`
    }
    return `${new Date().toLocaleTimeString()}: ${message}`;
  }
}
