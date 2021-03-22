export interface ResultIndicatorCodeRelation {
  code: string;
  name: string;
  measurementUnit: string;
}

export class ProgrammeResultIndicatorConstants {
  static indicatorCodes: ResultIndicatorCodeRelation[] = [
    {
      code: 'RCR001',
      name: 'Jobs created in supported entities',
      measurementUnit: 'annual FTEs'
    },
    {
      code: 'RCR002',
      name: 'Private investments matching public support (of which: grants, financial instruments)',
      measurementUnit: 'euro'
    },
    {
      code: 'RCR003',
      name: 'Small and medium-sized enterprises (SMEs) introducing product or process innovation',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR004',
      name: 'SMEs introducing marketing or organisational innovation',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR005',
      name: 'SMEs innovating in-house',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR006',
      name: 'Patent applications submitted',
      measurementUnit: 'patent applications'
    },
    {
      code: 'RCR007',
      name: 'Trademark and design applications',
      measurementUnit: 'trademark and design applications'
    },
    {
      code: 'RCR008',
      name: 'Publications from supported projects',
      measurementUnit: 'publications'
    },
    {
      code: 'RCR011',
      name: 'Users of new and upgraded public digital services, products and processes',
      measurementUnit: 'annual users'
    },
    {
      code: 'RCR012',
      name: 'Users of new and upgraded digital services, products and processes developed by enterprises',
      measurementUnit: 'annual users'
    },
    {
      code: 'RCR013',
      name: 'Enterprises reaching high digital intensity',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR017',
      name: 'New enterprises surviving in the market',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR018',
      name: 'SMEs using incubator services after incubator creation',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR019',
      name: 'Enterprises with higher turnover',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR025',
      name: 'SMEs with higher value added per employee',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR026',
      name: 'Annual primary energy consumption (of which: dwellings, public buildings, enterprises, other)',
      measurementUnit: 'MWh/year'
    },
    {
      code: 'RCR029',
      name: 'Estimated greenhouse gas emissions',
      measurementUnit: 'tons of CO2eq/year'
    },
    {
      code: 'RCR031',
      name: 'Total renewable energy produced (of which: electricity, thermal)',
      measurementUnit: 'MWh/year'
    },
    {
      code: 'RCR032',
      name: 'Additional operational capacity installed for renewable energy',
      measurementUnit: 'MW'
    },
    {
      code: 'RCR033',
      name: 'Users connected to smart energy systems',
      measurementUnit: 'end users/year'
    },
    {
      code: 'RCR034',
      name: 'Roll-out of projects for smart energy systems',
      measurementUnit: 'projects'
    },
    {
      code: 'RCR035',
      name: 'Population benefiting from flood protection measures',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR036',
      name: 'Population benefiting from wildfire protection measures',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR037',
      name: 'Population benefiting from protection measures against climate related natural disaster (other than flood and wildfires)',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR042',
      name: 'Population connected to at least secondary public waste water treatment',
      measurementUnit: 'population equivalent'
    },
    {
      code: 'RCR043',
      name: 'Water losses in distribution systems for public water supply',
      measurementUnit: 'cubic metres per year'
    },
    {
      code: 'RCR047',
      name: 'Waste recycled',
      measurementUnit: 'tonnes/year'
    },
    {
      code: 'RCR048',
      name: 'Waste used as raw materials',
      measurementUnit: 'tonnes/year'
    },
    {
      code: 'RCR050',
      name: 'Population benefiting from measures for air quality',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR052',
      name: 'Rehabilitated land used for green areas, social housing, economic or other uses',
      measurementUnit: 'hectares'
    },
    {
      code: 'RCR053',
      name: 'Dwellings with broadband subscriptions to a very high capacity network',
      measurementUnit: 'dwellings'
    },
    {
      code: 'RCR054',
      name: 'Enterprises with broadband subscriptions to a very high capacity network',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR055',
      name: 'Annual users of newly built, reconstructed, upgraded or modernised roads',
      measurementUnit: 'passenger-km/year'
    },
    {
      code: 'RCR056',
      name: 'Time savings due to improved road infrastructures',
      measurementUnit: 'man-days/year'
    },
    {
      code: 'RCR058',
      name: 'Annual users of newly, built, upgraded, reconstructed or modernised railways',
      measurementUnit: 'passenger-km/year'
    },
    {
      code: 'RCR059',
      name: 'Freight transport on rail',
      measurementUnit: 'tonnes-km/year'
    },
    {
      code: 'RCR060',
      name: 'Freight transport on inland waterways',
      measurementUnit: 'tonnes-km/year'
    },
    {
      code: 'RCR062',
      name: 'Annual users of new or modernised public transport',
      measurementUnit: 'users'
    },
    {
      code: 'RCR063',
      name: 'Annual users of new or modernised tram and metro lines',
      measurementUnit: 'users'
    },
    {
      code: 'RCR064',
      name: 'Annual users of dedicated cycling infrastructure',
      measurementUnit: 'users'
    },
    {
      code: 'RCR065',
      name: 'Annual users of new or modernised facilities for employment services',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR066',
      name: 'Annual users of new or modernised temporary reception facilities',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR067',
      name: 'Annual users of new or modernised social housing',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR070',
      name: 'Annual users of new or modernised childcare',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR071',
      name: 'Annual users of new or modernised education facilities',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR072',
      name: 'Annual users of new or modernised e-health care services',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR073',
      name: 'Annual users of new or modernised health care facilities',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR074',
      name: 'Annual users of new or modernised social care facilities',
      measurementUnit: 'users/year'
    },
    {
      code: 'RCR077',
      name: 'Visitors of cultural and tourism sites supported',
      measurementUnit: 'visitors/year'
    },
    {
      code: 'RCR079',
      name: 'Joint strategies and action plans taken up by organisations',
      measurementUnit: 'Joint strategy / action plan'
    },
    {
      code: 'RCR081',
      name: 'Completion of joint training schemes',
      measurementUnit: 'Participants Completion'
    },
    {
      code: 'RCR082',
      name: 'Legal or administrative obstacles across borders alleviated or resolved',
      measurementUnit: 'Obstacles resolved'
    },
    {
      code: 'RCR083',
      name: 'Persons covered by joint administrative or legal agreements signed',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR084',
      name: 'Organisations cooperating across borders after project completion',
      measurementUnit: 'organisations'
    },
    {
      code: 'RCR085',
      name: 'Participations in joint actions across borders after project completion',
      measurementUnit: 'Participation'
    },
    {
      code: 'RCR095',
      name: 'Population having access to new or improved green infrastructure',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR096',
      name: 'Population benefiting from protection measures against non-climate related natural risks and risks related to human activities',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR097',
      name: 'Apprenticeships supported in SMEs',
      measurementUnit: 'annual FTEs'
    },
    {
      code: 'RCR098',
      name: 'SMEs staff completing training for skills for smart specialisation, for industrial transition and entrepreneurship (by type of skill: technical, management, entrepreneurship, green, other)',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR101',
      name: 'Time savings due to improved rail infrastructures',
      measurementUnit: 'man-days/year'
    },
    {
      code: 'RCR102',
      name: 'Research jobs created in supported entities',
      measurementUnit: 'annual FTEs'
    },
    {
      code: 'RCR103',
      name: 'Waste collected separately',
      measurementUnit: 'tonnes/year'
    },
    {
      code: 'RCR104',
      name: 'Solutions taken up or up-scaled by organisations',
      measurementUnit: 'solutions applied'
    },
    {
      code: 'RCR105',
      name: 'Estimated greenhouse emissions by boilers and heating systems converted from solid fossil fuels to gas',
      measurementUnit: 'tons of CO2eq/year',
    },
  ];
}
