export enum ProjectPartnerRoleEnum {
  PARTNER = 'PARTNER',
  LEAD_PARTNER = 'LEAD_PARTNER',

}

export class ProjectPartnerRoleEnumUtil {
  public static toProjectPartnerRoleEnum(role: string): ProjectPartnerRoleEnum | null {
    switch (role) {
      case 'PARTNER':
        return ProjectPartnerRoleEnum.PARTNER;
      case 'LEAD_PARTNER':
        return ProjectPartnerRoleEnum.LEAD_PARTNER;
      default:
        return null;
    }
  }
}
