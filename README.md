# Meet-Me-Halfway-Workspace

Team workspace to share code snippets

## Components

* Android frontend
* REST Backend with Lambda functions, API Gateway, Elasticsearch (Geo-Point), Kinesis/Kafka, SNS, DynamoDB
* APIs - Google Maps, Google Places, ~~Twilio, Factual, Foursquare, Google Maps Directions API, Google Maps Distance Matrix API~~ Firebase, Facebook API, 

## Languages

* Android (No experience!)- **Java**/Kotlin 
* Backend Lambda- Python/Java, based on personal preference as Lambda functions can be in different languages.


## Notes

### Lambdas:

* ~~Retrieve friends~~ Register User - Iris and Kanishk
* New Request - Tory
* Accept/Reject Invite - Kanishk/Tory
* Recommendation Engine - Jean
* Confirmation - to avoid consensus problem, the proposer gets to decide final venue - Tory

### Database - Tables:

* Users
* Meetings

#### User table attributes

* ID (unique)
* Name
* ~~Email (unique)~~
* DOB
* Gender
* Phone
* Registration Token for push (unique)
* Default Location/Address
* Status
* Pending Invitations
* Accepted Invitations

#### Meetings attributes

* Meeting ID - Primary Index
* UserID/Organizer - Global Secondary Index
* PeopleInvited/Status
* Confirmation/Recommendations
* Status (Pending/Completed)

## Final individual Contributions
* **Android** app with Facebook, Google Places, Google Maps, Firebase, AWS (DynamoDB & API Gateways) integration - PeiTzu/Kanishk
* Lambdas: Recommendation Lambda - HuiChun, New Meeting & Confirmation - Tory, Accept/Reject Invite Lambda - Tory/Kanishk
* Architecture and Integration Design/Plan (Backend and Android)- Kanishk/Pei-Tzu
