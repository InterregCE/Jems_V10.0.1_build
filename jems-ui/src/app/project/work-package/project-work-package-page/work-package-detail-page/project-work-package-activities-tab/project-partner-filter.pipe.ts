import {Pipe, PipeTransform} from '@angular/core';
import {ProjectPartner} from '@project/model/ProjectPartner';

@Pipe({name: 'filterPartner', pure: true})
export class ProjectPartnerFilterPipe implements PipeTransform {

  transform(partners: ProjectPartner[], filterText: string): ProjectPartner[] {
    if (!filterText) {
      return partners;
    }

    return partners.filter(partner =>
      partner.abbreviation.toUpperCase().startsWith(filterText.toUpperCase())
      || partner.partnerNumber.toUpperCase().startsWith(filterText.toUpperCase())
    );
  }

}
