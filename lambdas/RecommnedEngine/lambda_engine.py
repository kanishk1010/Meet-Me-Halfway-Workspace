import json, time, datetime
import config
import googlePlace
from medpoint import ElasticsearchWrapper
from kafka import KafkaConsumer
from pyfcm import FCMNotification

def lambda_handler(event, context): 
	time.sleep(10)
	consumer = KafkaConsumer(bootstrap_servers=config.KAFKA_SERVER,
				group_id='lambda_recommend_engine',
				consumer_timeout_ms=30000,
				auto_offset_reset='earliest',
				value_deserializer=lambda m: json.loads(m.decode('utf-8')))
	consumer.subscribe(['recommend_query'])

	for message in consumer:
		jsonData = json.loads(message.value)
		#parse input
		registrationID = jsonData['registrationID']
		date = jsonData['meetingDate'] #'12/15/2017'
		month, day, year = (int(x) for x in date.split('/'))    
		day = datetime.date(year, month, day).isoweekday() #0~6
		start = jsonData['meetingTime'] #HH:MM
		start = start[:2] + start[3:] #1200
		end = int(start) + 100 #1300
		preference_list = jsonData['preference']#['chinese', 'indian']

		elasticsearch = ElasticsearchWrapper()
		for entry in jsonData['locations']:
			elasticsearch.upload(json.dumps(entry))
		time.sleep(1)
		centroid = elasticsearch.search_centroid()
		lat = centroid['aggregations']['centroid']['location']['lat']
		lon = centroid['aggregations']['centroid']['location']['lon']
		print lat
		print lon
		placeId_list = googlePlace.google_nearby(day, start, end, lat, lon, 200, preference_list)
		elasticsearch.delete_index()

		jsonOut = {
			"meetingID" : jsonData['meetingID'],
			"placeIdList" : placeId_list
		}
		print json.dumps(jsonOut)

		push_service = FCMNotification(api_key="")
		result = push_service.notify_single_device(registration_id = registrationID, 
			message_body = json.dumps(jsonOut), data_message = data_message)
		print result

