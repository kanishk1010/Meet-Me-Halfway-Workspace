import boto3
from pyfcm import FCMNotification
from decimal import *
from kafka import SimpleProducer, KafkaClient
import json

import logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event, context):

    # logger.info(event['body'])
    try:
        data = json.loads(event['body'])
    except Exception:
        data = event['body']
    meetingId = data['MeetingId']
    userId = data['UserID']
    isAccepted = data['IsAccepted']
    push_service = FCMNotification(api_key="AAAAI7QSMgk:")
    mycontext = Context(prec=6, rounding=ROUND_HALF_DOWN)
    setcontext(mycontext)
    dynamodb = boto3.resource('dynamodb', region_name='us-east-1', endpoint_url="http://dynamodb.us-east-1.amazonaws.com")
    meeting_table = dynamodb.Table('MeetingTable')
    meeting_status_table = dynamodb.Table('MeetingStatus')
    user_table = dynamodb.Table('Users')

    sns = boto3.client('sns', region_name = "us-east-1")

    meeting_item = meeting_table.get_item(
        Key={
            'MeetingId': meetingId
        }
    )


    organizer = meeting_item['Item']['organizer']

    user_item = user_table.get_item(
        Key={
            'ID': organizer
        }
    )
    organizer_token = user_item['Item']["Token"]

    meeting_status_item = meeting_status_table.get_item(
        Key={
            'userID': userId
        }
    )

    kafka_data_message = {}
    kafka_data_message['Locations'] = [{'location': {'lat': meeting_item['Item']['latitude'], 'lon': meeting_item['Item']['longitude']}}]

    kafka_data_message['Preference'] = meeting_item['Item']['preference']
    kafka_data_message['DeviceToken'] = organizer_token
    kafka_data_message['meetingId'] = meetingId
    kafka_data_message['meetingDate'] = meeting_item['Item']['meetingDate']
    kafka_data_message['meetingTime'] = meeting_item['Item']['meetingTime']

    userName = data['userName']


    # if user accepts this invitation
    if isAccepted:
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
        AcceptStatus[userId] = [data['latitude'], data['longitude']]
        meeting_table.update_item(
            Key={
                'MeetingId': meetingId,
            },
            UpdateExpression='SET acceptStatus = :val',
            ExpressionAttributeValues={
                ':val': AcceptStatus
            }
        )
        flag = True

        for key in AcceptStatus:
            if  len(AcceptStatus[key]) == 0:
                flag = False
                break
            else:
                coordinate = {'location':{
                    "lat": AcceptStatus[key][0],
                    "lon": AcceptStatus[key][1]
                }}
                kafka_data_message['Locations'].append(coordinate)
        # All the member accept the invitation
        if flag:
            print (kafka_data_message)
            # send message to kafka
            client = KafkaClient("54.84.107.121:9092")
            producer = SimpleProducer(client, async = True,
                                      batch_send_every_n = 1000,
                                      batch_send_every_t = 10)
            producer.send_messages(b'recommend_query', json.dumps(kafka_data_message))
            sns.publish(TopicArn = 'arn:aws:sns:us-east-1:710467018335:NotifyRecomEngine', MessageStructure='JSON', Message=json.dumps({'default': json.dumps(kafka_data_message)}))

    else:

        push_service.notify_single_device(registration_id=organizer_token,
                                          message_body="Your invitation was rejected by "+userName,
                                          message_title="Meet Me Halfway")
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
        logger.info(meeting_item)
        AcceptStatus = meeting_item['Item']['acceptStatus']
        if len(AcceptStatus) <= 1:
            # notify organizer that no one accepts the invitation
            push_service.notify_single_device(registration_id=organizer_token, message_body="All members have declined the invitation", message_title="Meet Me Halfway")
            print("No one accepts the invitation")
            meeting_table.update_item(
                Key={
                    'MeetingId': meetingId,
                },
                UpdateExpression='SET finalStatus = :val',
                ExpressionAttributeValues={
                    ':val': True
                }
            )
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
            flag = True
            for key in AcceptStatus:
                if len(AcceptStatus[key]) == 0:
                    flag = False
                    break
                coordinate = {'location':{
                    "lat": AcceptStatus[key][0],
                    "lon": AcceptStatus[key][1]
                }}
                kafka_data_message['Locations'].append(coordinate)
            # All the member accept the invitation
            if flag:
                # send message to kafka
                client = KafkaClient("54.84.107.121:9092")
                producer = SimpleProducer(client, async = True,
                                          batch_send_every_n = 1000,
                                          batch_send_every_t = 10)
                producer.send_messages(b'recommend_query', json.dumps(kafka_data_message))
                sns.publish(TopicArn = 'arn:aws:sns:us-east-1:710467018335:NotifyRecomEngine', MessageStructure='JSON', Message=json.dumps({'default': json.dumps(kafka_data_message)}))



