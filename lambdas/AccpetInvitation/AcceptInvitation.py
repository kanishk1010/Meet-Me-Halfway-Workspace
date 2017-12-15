import boto3
from pyfcm import FCMNotification
from decimal import *
from kafka import SimpleProducer, KafkaClient

def lambda_handler(event, context):

	# data = json.loads(json.dumps(event['queryStringParameters']))
	# meetingId = data['MeetingId']
	# userId = data['UserId']
	# isAccepted = data['IsAccepted']
	# deviceToken = data['DeviceToken']

	mycontext = Context(prec=6, rounding=ROUND_HALF_DOWN)
	setcontext(mycontext)
	userId = 'Mike'
	data = {}
	data['latitude'] = Decimal('1.3')
	data['longitude'] = Decimal('1.3')
	data['preference'] = 'Cafe'
	isAccepted = True
	dynamodb = boto3.resource('dynamodb', region_name='us-east-1', endpoint_url="http://dynamodb.us-east-1.amazonaws.com")
	table = dynamodb.Table('MeetingTable')

	response = table.get_item(
		Key={
	        'MeetingId': 'lala@2017-12-13 03:07:50',
	    }
	)
	# if user accpet this invitation
	if isAccepted:
		AcceptStatus = response['Item']['acceptStatus']
		userAcceptStatus = AcceptStatus[userId]
		userAcceptStatus['status'] = True
		flag = True

		kafka_data_message = {}
		kafka_data_message['Locations'] = []
		kafka_data_message['Preference'] = []
		for key in AcceptStatus:
			if not AcceptStatus[key]['status']:
				flag = False
				break
			if 'coordinate' in AcceptStatus[key]:
				coordinate = {
					"latitude": AcceptStatus[key]['coordinate']['latitude'],
					"longitude": AcceptStatus[key]['coordinate']['longitude']
				}
				kafka_data_message['Locations'].append(coordinate)
				kafka_data_message['Preference'].append(AcceptStatus[key]['preference'])
		# All the member accept the invitation
		if flag:
			# send message to kafka
			kafka = KafkaClient("localhost:9092")
			producer = SimpleProducer(client, async = True,
                          batch_send_every_n = 1000,
                          batch_send_every_t = 10)
			producer.send_messages(b'Tweets', json.dumps(kafka_data_message))
			print("lal")
		else:
			AcceptStatus[userId]['coordinate'] = {
				'latitude': Decimal(data['latitude']),
				'longitude': Decimal(data['longitude'])
			}
			print(AcceptStatus[userId])
			AcceptStatus[userId]['preference'] = data['preference']
			AcceptStatus[userId]['status'] = True
			table.update_item(
				Key={
			        'MeetingId': 'lala@2017-12-13 03:07:50',
			    },
			    UpdateExpression='SET acceptStatus = :val',
			    ExpressionAttributeValues={
			        ':val': AcceptStatus
			    }
			)
	else:
		AcceptStatus = response['Item']['acceptStatus']
		userAcceptStatus = AcceptStatus[userId]
		if len(AcceptStatus) <= 1:
			# notify organizer that no one accepts the invitation
			push_service = FCMNotification(api_key="AAAAI7QSMgk:APA91bGiQ4Cbi9L1dnDyixObLdIOiBVBeBpwuiR8y0Am1WoUXK09UoM6uJCXo2S_gzqFycVbhd9YADyn4hYBdcNWqTfOqA2Ew_xgjz7eK5cqCOBkq3fvmCZbwOWIVWoqQkVJhsCHqkhr")
			data_message = {
					'EventTpe': 'AcceptInvitation',
					'Response': 'All members decline the invitation'
			}
			result = push_service.notify_single_device(registration_id = "f-Fd1NnGd2w:APA91bEBgkS3XFxjx7kcGR-aTSpujFuogQKQP2VKFKPVueb5mp8sqoJ-850w3AHNr_3xFTqFt9mbEpd4qH4cNnok0rQyNdR38vunvevmxlmEsnZYSylluDlsN1QzeRmiwyJxOlDekuCX", message_body = "HelloWorld", data_message = data_message)
			print("No one accepts the invitation")
		else:
			AcceptStatus.pop(userId, None)
			table.update_item(
				Key={
			        'MeetingId': 'lala@2017-12-13 03:07:50',
			    },
			    UpdateExpression='SET acceptStatus = :val',
			    ExpressionAttributeValues={
			        ':val': AcceptStatus
			    }
			)

			# if all the members accept the invitation
			kafka_data_message = {}
			kafka_data_message['Locations'] = []
			kafka_data_message['Preference'] = []
			for key in AcceptStatus:
				if not AcceptStatus[key]['status']:
					flag = False
					break
				coordinate = {
					"latitude": AcceptStatus[key]['coordinate']['latitude'],
					"longitude": AcceptStatus[key]['coordinate']['longitude']
				}
				kafka_data_message['Locations'].append(coordinate)
				kafka_data_message['Preferences'].append(AcceptStatus[key]['preference'])
			# All the member accept the invitation
			if flag:
				# send message to kafka
				kafka = KafkaClient("localhost:9092")
				producer = SimpleProducer(client, async = True,
	                          batch_send_every_n = 1000,
	                          batch_send_every_t = 10)
				producer.send_messages(b'Tweets', json.dumps(kafka_data_message))




	HTTPResponse = {}
	HTTPResponse['statusCode'] = 200
	HTTPResponse['headers'] = {}
	HTTPResponse['body'] = response

if __name__ == '__main__':
    lambda_handler(None, None)