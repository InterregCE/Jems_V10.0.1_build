export enum LumpSumPhaseEnum {
  PREPARATION = 'Preparation',
  IMPLEMENTATION = 'Implementation',
  CLOSURE = 'Closure'
}

export class LumpSumPhaseEnumUtils {
  public static toLumpSumPhaseEnum(phase: string): LumpSumPhaseEnum | null {
    switch (phase) {
      case 'Preparation':
        return LumpSumPhaseEnum.PREPARATION;
      case 'IMPLEMENTATION':
        return LumpSumPhaseEnum.IMPLEMENTATION;
      case 'CLOSURE':
        return LumpSumPhaseEnum.CLOSURE;
      default:
        return null;
    }
  }
}
