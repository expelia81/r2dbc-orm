# R2dbc Object Mapper (R2O)
- This is a library that provides a simple and easy way to map the results of a query to an entity object.
- R2O is a simple library that operates based on the Database Client, an R2dbc driver, and maps query results into an entity similar to JPA.

# You can use like JPA, but it is not a JPA.
- R2O only provides viewability functionality.

### Join Mapping Rules
- ManyToMany and OneToMany are searched only on the entity being searched.
- ManyToOne provides continuous lookup across all relationships.

### Caution
- Entity must have a default constructor.
- Entity must have a equals (uniqueness must be proven, and it is recommended to base it on the id field.)
- Entity must have a @R2dbcTable annotation.
- If aliases overlap, they will no longer be searched. If an entity is called repeatedly in multiple relationships, set the alias for calling other entities, such as ManyToOne, OneToMany, etc., different from the entity's alias.



## Functions
- all functions are based on the DatabaseClient.

### findAll  (normal / pageable)
- findAll
### findById  (normal / pageable)
### findByFilter  (normal / pageable)
- Filter is map<String,String>.