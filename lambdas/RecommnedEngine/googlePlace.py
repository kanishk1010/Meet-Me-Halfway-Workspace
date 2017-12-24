import urllib, json

class Interval(): 
	def __init__(self, start, end): # closed interval, [start,end]
		self.start = start
		self.end = end

	def subset(self, other):
		"@return: True iff self is subset of other."
		return self.start >= other.start and self.end <= other.end

def google_nearby(day, start, end, lat, lng, radius, preference_list):
	AUTH_KEY = ''
	LOCATION = str(lat) + "," + str(lng)
	RADIUS = radius #'rankby=distance', rankby must not be included if radius is specified.	
	KEYWORD = ''
	for word in preference_list:
		KEYWORD += 'OR' + word
	url = ('https://maps.googleapis.com/maps/api/place/nearbysearch/json?'
			'location=%s'
			'&radius=%s'
			'&types=restaurant'
			'&keyword=%s'
			'&key=%s'
			)% (LOCATION, RADIUS, KEYWORD[2:], AUTH_KEY)
	print url
	response = urllib.urlopen(url)
	jsonRaw = response.read()
	jsonData = json.loads(jsonRaw)

	place_id_pool = []
	for place in jsonData['results']:
		# print place['place_id'] + ' ' + place['name']
		if isAvailable(day, start, end, place['place_id']):
			place_id_pool.append(place['place_id'])
			# place_id_pool.append(place['name'])

	# set the max number of place_id: 10
	if len(place_id_pool) <= 10:
		return place_id_pool
	else:
		return place_id_pool[:10]

def getPlace_details(place_id):
	AUTH_KEY = ''
	url = ('https://maps.googleapis.com/maps/api/place/details/json?'
			'place_id=%s'
			'&key=%s'
			)% (place_id, AUTH_KEY)
	response = urllib.urlopen(url)
	jsonRaw = response.read()
	return jsonRaw


# day: 0 to 6, starting on Sunday. e.g. 2 is Tuesday.
# time: 0000 to 2359
def isOpen(day, start, end, jsonRaw):
	jsonData = json.loads(jsonRaw)
	requestTime = Interval(start, end)

	if 'opening_hours' not in jsonData['result']:
		return True
	
	for period in jsonData['result']['opening_hours']['periods']:
		if period['open']['day'] == day:
			# print period
			interval_start = int(period['open']['time'])
			if period['close']['time'] == '0000': # always open, the close section will be missing from the response. 
				interval_end = 2400
			else:
				interval_end = int(period['close']['time'])
			interval = Interval(interval_start, interval_end)
			# print str(interval.start)+ ' ' + str(interval.end)
			if requestTime.subset(interval):
				return True
	return False		

def isAvailable(day, start, end, place_id):
	# print place_id
	jsonRaw = getPlace_details(place_id)
	return isOpen(day, start, end, jsonRaw)

# if __name__ == '__main__':
# 	preference_list = ['chinese', 'indian']
# 	recommend_list = google_nearby(3, 1200, 1300, 40.7293222658, -74.000301864, 200, preference_list)
# 	print recommend_list




