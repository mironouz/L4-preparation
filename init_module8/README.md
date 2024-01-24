## Build
```
./mvnw clean install
```
This will run the unit and integration tests also.

### Run the integration test with custom input file
```
./mvnw -Dstore.file.location=<path_to>.csv clean install
```

## Run
```
./mvnw exec:java
```

### Run the application with custom input file
```
./mvnw -Dstore.file.location=<path_to>.csv  exec:java
```

## Check coverage
```
./mvnw clean install jacoco:report
```
### View results
Open in any browser `target/site/jacoco/index.html`

## Test data file
Test data file must be valid CSV.
The test file can be specified through
- `src/main/resources/booking.properties` property `store.file.location`
- `-Dstore.file.location=<path_to>.csv` system property

### Format
The rows must be in semantically correct order, i.e. a `ticket` can be created only after the respective `user` and `event` was already created.

#### `user` format with sample
```
type,id,name,email
user,1,User1,user1@email.org
```

#### `event` format with sample
```
type,id,title,date
event,3,Event3,2023-02-04
```
where `date` pattern is `yyyy-mm-dd`

#### `ticket` format with sample
```
type,id,category,userId,eventId,place
ticket,8,BAR,1,5,5
```
where `category` in `['BAR', 'STANDARD', 'PREMIUM']`
