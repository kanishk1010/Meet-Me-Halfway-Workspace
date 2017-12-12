# Meet-Me-Halfway-Workspace

Team workspace to share code snippets

## Components

* Android frontend
* REST Backend with Lambda functions, API Gateway, Elasticsearch (Geo-Point), Kinesis, SNS, DynamoDB
* APIs - Google Maps, Google Places, Twilio, Factual, Foursquare, Google Maps Directions API, Google Maps Distance Matrix API

## Languages

* Android - **Java**/Kotlin
* Backend - Python/Java, based on personal preference as Lambda functions can be in different languages.

## To-Do

* Pick up the components and create code snippets testing each of the APIs and AWS components, Keeping Lambda Functions in mind (every API call can be a lambda function through API Gateway).
* 

## Notes

### Lambdas:

* ~~Retrieve friends~~ Register User - Iris and Kanishk
* New Request - Tory
* Accept Invite - Kanishk
* Recommendation Engine - Jean
* Confirmation - to Reach consensus on final decision

### Database - Tables:

* User
* Meetings

#### User table attributes

* ID (unique)
* Name
* Email (unique)
* Registration Token for push (unique)
* Status (Active/Inactive)
* Default Location/Address

#### Meetings attributes

* Meeting ID 
* UserID 
* Count of User Involved
* Count of User Accepted (starts with 1 for the initial request)
* Status (Pending/Completed)

Note: None of the entries are unique

