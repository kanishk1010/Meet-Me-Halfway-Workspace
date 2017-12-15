from datetime import datetime
import boto3
import json
from pyfcm import FCMNotification

def lambda_handler(event, context):
    # TODO implement
    # print(json.dumps(event['queryStringParameters']))
    # data = json.loads(json.dumps(event['queryStringParameters']))
    # userId = data['userId']
    # meetingDate = data['meetingDate']
    # meetingFromWindow = data['meetingFromWindow']
    # meetingToWindow = data['meetingToWindow']
    # preference = data['preference']
    # Friends = data['Friends']
    
    # Friends = {}
    # preference = None
    # timeId = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    # meetingId = userId + "@" + timeId

    dynamodb = boto3.resource('dynamodb', region_name='us-east-1', endpoint_url="http://dynamodb.us-east-1.amazonaws.com")
    table = dynamodb.Table('MeetingTable')
    acceptStatus = {}
    acceptStatus['Tony'] = False
    acceptStatus['John'] = False


    # for friend in Friends:
    #     acceptStatus[friend['userId']] = False

    # store meeting info into DynamoDB
    res = table.put_item(
        Item = {
            'MeetingId': "lala@2017-12-13 03:07:50",
            'organizer': 'Jack',
            'meetingFromWindow': "meetingFromWindow",
            'meetingToWindow': "meetingToWindow",
            'preference': "preference",
            'acceptStatus': acceptStatus,
            'Confirmation': None
        }
    )
    
    push_service = FCMNotification(api_key="AAAAI7QSMgk:APA91bGiQ4Cbi9L1dnDyixObLdIOiBVBeBpwuiR8y0Am1WoUXK09UoM6uJCXo2S_gzqFycVbhd9YADyn4hYBdcNWqTfOqA2Ew_xgjz7eK5cqCOBkq3fvmCZbwOWIVWoqQkVJhsCHqkhr")
    # # for friend in Friends:
    # # item = {
    # #         'MeetingId': meetingId,
    # #         'organizer': userId,
    # #         'meetingFromWindow': meetingFromWindow,
    # #         'meetingToWindow': meetingToWindow,
    # #         'preference': preference,
    # #     }
    # result = push_service.notify_single_device(registration_id = "f-Fd1NnGd2w:APA91bEBgkS3XFxjx7kcGR-aTSpujFuogQKQP2VKFKPVueb5mp8sqoJ-850w3AHNr_3xFTqFt9mbEpd4qH4cNnok0rQyNdR38vunvevmxlmEsnZYSylluDlsN1QzeRmiwyJxOlDekuCX", message_body = "HelloWorld")
    # print(result)
    return 'Hello from Lambda'

if __name__ == '__main__':
    lambda_handler(None, None)



    from datetime import datetime
import boto3
import json
from pyfcm import FCMNotification

def lambda_handler(event, context):
    # TODO implement
    print(json.dumps(event['queryStringParameters']))
    data = json.loads(json.dumps(event['queryStringParameters']))
    userId = data['userId']
    meetingDate = data['meetingDate']
    meetingFromWindow = data['meetingFromWindow']
    meetingToWindow = data['meetingToWindow']
    preference = data['preference']
    Friends = data['Friends']
    
    # Friends = {}
    # preference = None
    timeId = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    meetingId = userId + "@" + timeId

    dynamodb = boto3.resource('dynamodb', region_name='us-east-1', endpoint_url="http://dynamodb.us-east-1.amazonaws.com")
    table = dynamodb.Table('MeetingTable')
    # acceptStatus = {}
    # acceptStatus['Tony'] = False
    # acceptStatus['John'] = False


    for friend in Friends:
        acceptStatus[friend['userId']] = {}
        acceptStatus[friend['userId']]['status'] = False
        

    # store meeting info into DynamoDB
    res = table.put_item(
        Item = {
            'MeetingId': "meetingId",
            'organizer': userId,
            'meetingFromWindow': meetingFromWindow,
            'meetingToWindow': meetingToWindow,
            'preference': preference,
            'acceptStatus': acceptStatus,
            'Confirmation': None
        }
    )
    
    push_service = FCMNotification(api_key="AAAAI7QSMgk:APA91bGiQ4Cbi9L1dnDyixObLdIOiBVBeBpwuiR8y0Am1WoUXK09UoM6uJCXo2S_gzqFycVbhd9YADyn4hYBdcNWqTfOqA2Ew_xgjz7eK5cqCOBkq3fvmCZbwOWIVWoqQkVJhsCHqkhr")
    for friend in Friends:
        message = {
                'MeetingId': meetingId,
                'organizer': userId,
                'meetingFromWindow': meetingFromWindow,
                'meetingToWindow': meetingToWindow,
                'preference': preference,
            }
        result = push_service.notify_single_device(registration_id = friend['deviceToken'], message_body = "HelloWorld", data_message = message)
    print(result)
    HTTPResponse = {}
    HTTPResponse['statusCode'] = 200
    HTTPResponse['headers'] = {}
    HTTPResponse['body'] = response
    logger.info(HTTPResponse)
    return {
        'statusCode': 200,
        'headers': { "Access-Control-Allow-Origin" : "*", },
        'body': json.dumps(HTTPResponse)
    }


