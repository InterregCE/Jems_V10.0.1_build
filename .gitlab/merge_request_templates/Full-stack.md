##### Description

##### Backend checklist
- [ ] Required annotations are added to each use-case
- `@Transactional`, `@ExceptionWrapper`, access control related annotation
- [ ] Input validation is done
- [ ] DTOs/Entities are not used in the use-case level
- [ ]  `mapstruct` is used for mappings (none trivial ones can be implemented in the mapper class instead of being abstract)
- [ ] Required migration scripts are added
- [ ] Required tests are added
##### Frontend checklist
- [ ]  `changeDetection` is set to `OnPush` for components (as much as possible)
- [ ] Auto generated services are not injected in the components
- [ ] Observables are added/used to/from the right store
- [ ] Custom styles are not added (as much as possible)
##### Common checklist
- [ ] Code is fulfilling the requirements in the story
- [ ] No obvious performance issue exists
- [ ] Has not introduced new solution for an already solved problem
- [ ] Translations are added for new translation keys
- [ ] Code is clean, readable and self-documenting
-  no code duplication, no comments (as mush as possible), good function names and etc
