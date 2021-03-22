export interface OutputIndicatorCodeRelation {
  code: string;
  name: string;
  measurementUnit: string;
}

export class ProgrammeOutputIndicatorConstants {

  static indicatorCodes: OutputIndicatorCodeRelation[] = [
    {
      code: 'RCO001',
      name: 'Enterprises supported (of which: micro, small, medium, large)',
      measurementUnit: 'enterprises',
    },
    {
      code: 'RCO002',
      name: 'Enterprises supported by grants',
      measurementUnit: 'enterprises',
    },
    {
      code: 'RCO003',
      name: 'Enterprises supported by financial instruments',
      measurementUnit: 'enterprises',
    },
    {
      code: 'RCO004',
      name: 'Enterprises with non-financial support',
      measurementUnit: 'enterprises',
    },
    {
      code: 'RCO005',
      name: 'New enterprises supported',
      measurementUnit: 'enterprises',
    },
    {
      code: 'RCO006',
      name: 'Researchers working in supported research facilities',
      measurementUnit: 'annual FTEs',
    },
    {
      code: 'RCO007',
      name: 'Research institutions participating in joint research projects',
      measurementUnit: 'research institutions',
    },
    {
      code: 'RCO008',
      name: 'Nominal value of research and innovation equipment',
      measurementUnit: 'euro',
    },
    {
      code: 'RCO010',
      name: 'Enterprises cooperating with research institutions',
      measurementUnit: 'enterprises',
    },
    {
      code: 'RCO013',
      name: 'Value of digital services, products and processes developed for enterprises',
      measurementUnit: 'euro',
    },
    {
      code: 'RCO014',
      name: 'Public institutions supported to develop digital services, products and processes',
      measurementUnit: 'public institutions',
    },
    {
      code: 'RCO015',
      name: 'Capacity of incubation created',
      measurementUnit: 'enterprises',
    },
    {
      code: 'RCO016',
      name: 'Participations of institutional stakeholders in entrepreneurial discovery process',
      measurementUnit: 'participations of institutional stakeholders',
    },
    {
      code: 'RCO018',
      name: 'Dwellings with improved energy performance',
      measurementUnit: 'dwellings',
    },
    {
      code: 'RCO019',
      name: 'Public buildings with improved energy performance',
      measurementUnit: 'square meters',
    },
    {
      code: 'RCO020',
      name: 'District heating and cooling network lines newly constructed and improved',
      measurementUnit: 'km',
    },
    {
      code: 'RCO022',
      name: 'Additional production capacity for renewable energy (of which: electricity, thermal)',
      measurementUnit: 'MW',
    },
    {
      code: 'RCO023',
      name: 'Digital management systems for smart energy systems',
      measurementUnit: 'systems',
    },
    {
      code: 'RCO024',
      name: 'Investments in new or upgraded disaster monitoring, preparedness, warning and response systems against natural disasters',
      measurementUnit: 'euro',
    },
    {
      code: 'RCO025',
      name: 'Coastal strip, river bank and lakeshore flood protection newly built or consolidated',
      measurementUnit: 'km',
    },
    {
      code: 'RCO026',
      name: 'Green infrastructure built or upgraded for adaptation to climate change',
      measurementUnit: 'hectares',
    },
    {
      code: 'RCO027',
      name: 'National and sub-national strategies addressing climate change adaptation',
      measurementUnit: 'strategies',
    },
    {
      code: 'RCO028',
      name: 'Area covered by protection measures against wildfires',
      measurementUnit: 'hectares',
    },
    {
      code: 'RCO030',
      name: 'Area covered by protection measures against wildfires',
      measurementUnit: 'hectares',
    },
    {
      code: 'RCO031',
      name: 'Length of new or upgraded pipes for the distribution systems of public water supply',
      measurementUnit: 'km',
    },
    {
      code: 'RCO032',
      name: 'New or upgraded capacity for waste water treatment',
      measurementUnit: 'population equivalent',
    },
    {
      code: 'RCO034',
      name: 'Additional capacity for waste recycling',
      measurementUnit: 'tonnes/year',
    },
    {
      code: 'RCO036',
      name: 'Green infrastructure supported for other purposes than adaptation to climate change',
      measurementUnit: 'hectares',
    },
    {
      code: 'RCO037',
      name: 'Surface of Natura 2000 sites covered by protection and restoration measures',
      measurementUnit: 'hectares'
    },
    {
      code: 'RCO038',
      name: 'Surface area of rehabilitated land supported',
      measurementUnit: 'hectares'
    },
    {
      code: 'RCO039',
      name: 'Area covered by systems for monitoring air pollution installed',
      measurementUnit: 'hectares'
    },
    {
      code: 'RCO041',
      name: 'Additional dwellings with broadband access of very high capacity',
      measurementUnit: 'dwellings'
    },
    {
      code: 'RCO042',
      name: 'Additional enterprises with broadband access of very high capacity',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCO043',
      name: 'Length of new or upgraded roads - TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO044',
      name: 'Length of new or upgraded roads –non-TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO045',
      name: 'Length of roads reconstructed or modernised - TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO046',
      name: 'Length of roads reconstructed or modernised - non-TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO047',
      name: 'Length of new or upgraded rail - TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO048',
      name: 'Length of new or upgraded rail - non-TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO049',
      name: 'Length of rail reconstructed or modernised - TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO050',
      name: 'Length of rail reconstructed or modernised – non-TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO051',
      name: 'Length of new, upgraded or modernised inland waterways - TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO052',
      name: 'Length of new, upgraded or modernised inland waterways –non-TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO053',
      name: 'New or modernised railway stations and stops',
      measurementUnit: 'stations and stops'
    },
    {
      code: 'RCO054',
      name: 'New or modernised intermodal connections',
      measurementUnit: 'intermodal connections'
    },
    {
      code: 'RCO055',
      name: 'Length of new tram and metro lines',
      measurementUnit: 'km'
    },
    {
      code: 'RCO056',
      name: 'Length of reconstructed or modernised tram and metro lines',
      measurementUnit: 'km'
    },
    {
      code: 'RCO057',
      name: 'Capacity of environmentally friendly rolling stock for collective public transport',
      measurementUnit: 'passengers'
    },
    {
      code: 'RCO058',
      name: 'Dedicated cycling infrastructure supported',
      measurementUnit: 'km'
    },
    {
      code: 'RCO059',
      name: 'Alternative fuels infrastructure (refuelling/ recharging points)',
      measurementUnit: 'refuelling/recharging points'
    },
    {
      code: 'RCO060',
      name: 'Cities and towns with new or modernised digitised urban transport systems',
      measurementUnit: 'cities and towns'
    },
    {
      code: 'RCO061',
      name: 'Surface of new or modernised facilities for employment services',
      measurementUnit: 'square metres'
    },
    {
      code: 'RCO063',
      name: 'Capacity of new or modernised temporary reception facilities',
      measurementUnit: 'persons'
    },
    {
      code: 'RCO065',
      name: 'Capacity of new or modernised social housing',
      measurementUnit: 'persons'
    },
    {
      code: 'RCO066',
      name: 'Classroom capacity of new or modernised childcare facilities',
      measurementUnit: 'persons'
    },
    {
      code: 'RCO067',
      name: 'Classroom capacity of new or modernised education facilities',
      measurementUnit: 'persons'
    },
    {
      code: 'RCO069',
      name: 'Capacity of new or modernised health care facilities',
      measurementUnit: 'persons/year'
    },
    {
      code: 'RCO070',
      name: 'Capacity of new or modernised social care facilities (other than housing)',
      measurementUnit: 'persons/year'
    },
    {
      code: 'RCO074',
      name: 'Population covered by projects in the framework of strategies for integrated territorial development',
      measurementUnit: 'persons'
    },
    {
      code: 'RCO075',
      name: 'Strategies for integrated territorial development supported',
      measurementUnit: 'strategies'
    },
    {
      code: 'RCO076',
      name: 'Integrated projects for territorial development',
      measurementUnit: 'projects'
    },
    {
      code: 'RCO077',
      name: 'Number of cultural and tourism sites supported',
      measurementUnit: 'cultural and tourism sites'
    },
    {
      code: 'RCO080',
      name: 'Community-led local development strategies supported',
      measurementUnit: 'strategies'
    },
    {
      code: 'RCO081',
      name: 'Participations in joint actions across borders',
      measurementUnit: 'participation'
    },
    {
      code: 'RCO082',
      name: 'Participations in joint actions promoting gender equality, equal opportunities and social inclusion',
      measurementUnit: 'participation'
    },
    {
      code: 'RCO083',
      name: 'Strategies and action plans jointly developed',
      measurementUnit: 'Strategy/action plan'
    },
    {
      code: 'RCO084',
      name: 'Pilot actions developed jointly and implemented in projects ',
      measurementUnit: 'pilot actions'
    },
    {
      code: 'RCO085',
      name: 'Participations in joint training schemes',
      measurementUnit: 'participation'
    },
    {
      code: 'RCO086',
      name: 'Joint administrative or legal agreements signed',
      measurementUnit: 'Legal or administrative agreement'
    },
    {
      code: 'RCO087',
      name: 'Organisations cooperating across borders',
      measurementUnit: 'organisations'
    },
    {
      code: 'RCO090',
      name: 'Projects for innovation networks across borders',
      measurementUnit: 'projects'
    },
    {
      code: 'RCO096',
      name: 'Interregional investments for innovation in EU projects',
      measurementUnit: 'euro'
    },
    {
      code: 'RCO097',
      name: 'Renewable energy communities supported',
      measurementUnit: 'renewable energy communities'
    },
    {
      code: 'RCO101',
      name: 'SMEs investing in skills for smart specialisation, for industrial transition and entrepreneurship',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCO103',
      name: 'High growth enterprises supported',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCO104',
      name: 'Number of high efficiency co-generation units',
      measurementUnit: 'co-generation units'
    },
    {
      code: 'RCO105',
      name: 'Solutions for electricity storage',
      measurementUnit: 'MWh'
    },
    {
      code: 'RCO106',
      name: 'Landslide protection newly built or consolidated',
      measurementUnit: 'hectares'
    },
    {
      code: 'RCO107',
      name: 'Investments in facilities for separate waste collection',
      measurementUnit: 'euro'
    },
    {
      code: 'RCO108',
      name: 'Length of roads with new or modernised traffic management systems – TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO109',
      name: 'Length of European Rail Traffic Management System equipped railways in operation – TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO110',
      name: 'Length of roads with new or modernised traffic management systems – non-TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO111',
      name: 'Length of European Rail Traffic Management System equipped railways in operation – non-TEN-T',
      measurementUnit: 'km'
    },
    {
      code: 'RCO112',
      name: 'Stakeholders involved in the preparation and implementation of strategies for integrated territorial development',
      measurementUnit: 'participations of institutional stakeholders'
    },
    {
      code: 'RCO113',
      name: 'Population covered by projects in the framework of integrated actions for socioeconomic inclusion of marginalised communities, low income households and disadvantaged groups',
      measurementUnit: 'persons'
    },
    {
      code: 'RCO114',
      name: 'Open space created or rehabilitated in urban areas',
      measurementUnit: 'square metres'
    },
    {
      code: 'RCO115',
      name: 'Public events across borders jointly organised',
      measurementUnit: 'events'
    },
    {
      code: 'RCO116',
      name: 'Jointly developed solutions',
      measurementUnit: 'solutions developed'
    },
    {
      code: 'RCO117',
      name: 'Solutions for legal or administrative obstacles across border identified',
      measurementUnit: 'solution identified for obstacles'
    },
    {
      code: 'RCO118',
      name: 'Organisations cooperating for the multi-level governance of macroregional strategies',
      measurementUnit: 'organisations'
    },
    {
      code: 'RCO119',
      name: 'Waste prepared for re-use',
      measurementUnit: 'tonnes/year'
    },
    {
      code: 'RCO120',
      name: 'Projects supporting cooperation across borders to develop urban-rural linkages',
      measurementUnit: 'projects'
    },
    {
      code: 'RCO121',
      name: 'Area covered by protection measures against climate related natural disasters (other than floods and wildfire)',
      measurementUnit: 'hectares'
    },
    {
      code: 'RCO122',
      name: 'Investments in new or upgraded disaster monitoring, preparedness, warning and response systems against non-climate related natural risks and risks related to human activities',
      measurementUnit: 'euro'
    },
    {
      code: 'RCO123',
      name: 'Dwellings benefiting from natural gas-fired boilers and heating systems replacing solid fossil fuels based installations',
      measurementUnit: 'dwellings'
    },
    {
      code: 'RCO124',
      name: 'Gas transmission and distribution network lines newly constructed or improved',
      measurementUnit: 'km'
    },
  ];

}
