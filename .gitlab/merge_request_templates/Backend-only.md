##### Description
- [ ] Thoroughly read the related story and the test steps before reviewing
- [ ] **MR creator: Please add pointers for the reviewer! Anything they should look out for in this merge?**
##### Checklist
- [ ] Code is fulfilling the requirements in the story
- [ ] Solution was checked **with story owner** in a call before merging
- [ ] Required annotations are added to each use-case
- `@Transactional`, `@ExceptionWrapper`, access control related annotation
- [ ] Input validation is done
- [ ] DTOs/Entities are not used in the use-case level
- [ ]  `mapstruct` is used for mappings (none trivial ones can be implemented in the mapper class instead of being abstract)
- [ ] Translations are added for new translation keys
- [ ] Required migration scripts are added
- [ ] Required tests are added
- [ ] No obvious performance issue exists
- [ ] Has not introduced new solution for an already solved problem
- [ ] Code is clean, readable and self-documenting
- [ ] No code duplication, no comments (as mush as possible), good function names and etc
