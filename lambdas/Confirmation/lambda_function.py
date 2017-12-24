import boto3
import json
from pyfcm import FCMNotification

def lambda_handler(event, context):
    # TODO implement
	data = json.loads(json.dumps(event['queryStringParameters']))
	meetingId = data['meetingId']
	placeId = data['placeId']
	dynamodb = boto3.resource('dynamodb', region_name='us-east-1', endpoint_url="http://dynamodb.us-east-1.amazonaws.com")
	table = dynamodb.Table('MeetingTable')
	
	table.update_item(
	        Key={
	            'MeetingId': meetingId,
	        },
	        UpdateExpression='SET finalLocation = :val, Status = :val2',
	        ExpressionAttributeValues={
			    ':val': placeId,
			    ':val2': true
			}
	    )
	    
	meeting = table.get_item({
		Key={
			'MeetingId': meetingId,
		},
	})
	
	friends = meeting['Item']['acceptStatus']
	
	push_service = FCMNotification(api_key="")
	for friend in friends:
    	table = dynamodb.Table('Users')
		response = table.get_item(
			Key={
		        'ID': friend,
		    }
		)
		deviceToken = response['Item']['Token']
    	result = push_service.notify_single_device(registration_id = deviceToken, message_body = "The meeting has been confirmed")
	    
    return 'Hello from Lambda'
