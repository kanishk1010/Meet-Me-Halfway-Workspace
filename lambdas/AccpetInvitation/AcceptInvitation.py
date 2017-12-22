import boto3
from pyfcm import FCMNotification
from decimal import *
from kafka import SimpleProducer, KafkaClient
import json

import logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event, context):

    logger.info("here")
    data = event['body']
    meetingId = data['MeetingId']
    userId = data['UserId']
    isAccepted = data['IsAccepted']
    # deviceToken = data['DeviceToken']
    push_service = FCMNotification(api_key="AAAAI7QSMgk:APA91bGiQ4Cbi9L1dnDyixObLdIOiBVBeBpwuiR8y0Am1WoUXK09UoM6uJCXo2S_gzqFycVbhd9YADyn4hYBdcNWqTfOqA2Ew_xgjz7eK5cqCOBkq3fvmCZbwOWIVWoqQkVJhsCHqkhr")
    mycontext = Context(prec=6, rounding=ROUND_HALF_DOWN)
    setcontext(mycontext)
    # userId = '1567685709934136'
    # data = {}
    # data['latitude'] = Decimal('1.3')
    # data['longitude'] = Decimal('1.3')
    # data['preference'] = 'Cafe'
    # isAccepted = True
    dynamodb = boto3.resource('dynamodb', region_name='us-east-1', endpoint_url="http://dynamodb.us-east-1.amazonaws.com")
    meeting_table = dynamodb.Table('MeetingTable')
    meeting_status_table = dynamodb.Table('MeetingStatus')
    user_table = dynamodb.Table('Users')

    # sns = boto3.client('sns',aws_access_key_id= "AKIAIJSA22F7CLH7K7IQ",aws_secret_access_key="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", region_name = "us-east-1")
    # jsonData = {
    # 	"meetingID" : 0001,
    # 	"meetingDate" : "12/15/2017",
    # 	"meetingTime" : "12:00",
    # 	"preference" : ["Indian", "Chinese"],
    # 	"locations" : [
    # 		{
    # 			"location" : {
    # 				"lat" : 40.7295174,
    # 				"lon" : -73.9986496
    # 			}
    # 		},
    # 		{
    # 			"location" : {
    # 				"lat" : 40.7291272,
    # 				"lon" : -74.0019541
    # 			}
    # 		},
    # 		{
    # 			"location" : {
    # 				"lat" : 40.7260051,
    # 				"lon" : -74.0007954
    # 			}
    # 		}
    # 	]
    # }
    meeting_item = meeting_table.get_item(
         Key={
            'MeetingId': meetingId
        }
    )
    meeting_status_item = meeting_status_table.get_item(
        Key={
            'userID': userId
        }
    )
    organizer_data = user_table.get_item(
        Key={
            'ID': meeting_item['Item']['organizer'],
        }
    )
    organizer_token = organizer_data['Item']['Token']

    # if user accepts this invitation
    if isAccepted:
        print(meeting_item)
        userName = data['userName']
        push_service.notify_single_device(registration_id=organizer_token, 
                                          message_body="Your invitation was accepted by "+userName, 
                                          message_title="Meet Me Halfway")
        user_pending_meetings = meeting_status_item['Item']['pending']
        user_accepted_meetings = meeting_status_item['Item']['accepted']
        user_accepted_meetings.append(meetingId)
        if meetingId in user_pending_meetings:
            user_pending_meetings.remove(meetingId)
        meeting_status_table.update_item(
            Key={
                'userID': userId
            },
            UpdateExpression='set pending = :p, accepted = :a',
            ExpressionAttributeValues={
                ':p': user_pending_meetings,
                ':a': user_accepted_meetings
            }
        )
        AcceptStatus = meeting_item['Item']['acceptStatus']
        AcceptStatus[userId] = [Decimal(data['latitude']), Decimal(data['longitude'])]
        flag = True

        kafka_data_message = {}
        kafka_data_message['Locations'] = []
        kafka_data_message['Preference'] = meeting_item['Item']['preference']
        kafka_data_message['DeviceToken'] = data['DeviceToken']
        for key in AcceptStatus:
            if  len(AcceptStatus[key]) == 0:
                flag = False
                break
            else:
                coordinate = {
                    "latitude": AcceptStatus[key][0],
                    "longitude": AcceptStatus[key][1]
                }
                kafka_data_message['Locations'].append(coordinate)
        # All the member accept the invitation
        if flag:
            # send message to kafka
            client = KafkaClient("54.84.107.121:9092")
            producer = SimpleProducer(client, async = True,
                                      batch_send_every_n = 1000,
                                      batch_send_every_t = 10)
            producer.send_messages(b'recommend_query', json.dumps(kafka_data_message))
            # sns.publish(TopicArn = 'arn:aws:sns:us-east-1:710467018335:NotifyRecomEngine',Subject = 'Hello', MessageStructure="json", Message=json.dumps({"default":json.dumps(jsonData)}))
        else:
            print(AcceptStatus[userId])
            # AcceptStatus[userId]['preference'] = data['preference']
            meeting_table.update_item(
                Key={
                    'MeetingId': meetingId,
                },
                UpdateExpression='SET acceptStatus = :val',
                ExpressionAttributeValues={
                    ':val': AcceptStatus
                }
            )
    else:
        user_pending_meetings = meeting_status_item['Item']['pending']
        if meetingId in user_pending_meetings:
            user_pending_meetings.remove(meetingId)
        meeting_status_table.update_item(
            Key={
                'userID': userId
            },
            UpdateExpression='set pending = :p',
            ExpressionAttributeValues={
                ':p': user_pending_meetings
            }
        )
        AcceptStatus = meeting_item['Item']['acceptStatus']
        if len(AcceptStatus) <= 1:
            # notify organizer that no one accepts the invitation
            push_service.notify_single_device(registration_id=organizer_token, message_body="All members have declined the invitation", message_title="Meet Me Halfway")
            print("No one accepts the invitation")
        else:
            AcceptStatus.pop(userId, None)
            meeting_table.update_item(
                Key={
                    'MeetingId': meetingId,
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
            flag = True
            for key in AcceptStatus:
                if len(AcceptStatus[key]) == 0:
                    flag = False
                    break
                coordinate = {
                    "latitude": AcceptStatus[key][0],
                    "longitude": AcceptStatus[key][1]
                }
                kafka_data_message['Locations'].append(coordinate)
                # kafka_data_message['Preferences'].append(AcceptStatus[key]['preference'])
            # All the member accept the invitation
            if flag:
                # send message to kafka
                client = KafkaClient("54.84.107.121:9092")
                producer = SimpleProducer(client, async = True,
                                          batch_send_every_n = 1000,
                                          batch_send_every_t = 10)
                producer.send_messages(b'recommend_query', json.dumps(kafka_data_message))




# HTTPResponse = {}
# HTTPResponse['statusCode'] = 200
# HTTPResponse['headers'] = {}
# HTTPResponse['body'] = response

