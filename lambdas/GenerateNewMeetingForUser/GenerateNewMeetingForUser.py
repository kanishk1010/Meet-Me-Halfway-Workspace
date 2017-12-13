from datetime import datetime
import boto3
import json

def lambda_handler(event, context):
    # TODO implement
    print(json.dumps(event['queryStringParameters']))
    data = json.loads(json.dumps(event['queryStringParameters']))
    userId = data['userId']
    meetingDate = data['meetingDate']
    meetingFromWindow = data['meetingFromWindow']
    meetingToWindow = data['meetingToWindow']
    preference = data['preference']
    # Friends = data['Friends']
    
    Friends = {}
    preference = None
    timeId = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    meetingId = userId + "@" + timeId

    dynamodb = boto3.resource('dynamodb', region_name='us-east-1', endpoint_url="http://dynamodb.us-east-1.amazonaws.com")
    table = dynamodb.Table('MeetingTable')
    acceptStatus = {}


    for friend in Friends:
        acceptStatus[friend['userId']] = False

    # store meeting info into DynamoDB
    res = table.put_item(
        Item = {
            'MeetingId': meetingId,
            'organizer': userId,
            'meetingFromWindow': meetingFromWindow,
            'meetingToWindow': meetingToWindow,
            'preference': preference,
        }
    )
    print(res)
    return 'Hello from Lambda'
