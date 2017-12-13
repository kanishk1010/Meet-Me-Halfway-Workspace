import urllib, json

# Support multiple keyword in preference
def google_nearby(lat, lng, radius, *keywords):
	AUTH_KEY = 'AIzaSyDTVUPaZKq_bJmvR3GF69xNWoZzbvMrFB8'
	LOCATION = str(lat) + "," + str(lng)
	RADIUS = radius #'rankby=distance', rankby must not be included if radius is specified.
	KEYWORD = ''
	for word in keywords:
		KEYWORD += 'OR' + word
	MyUrl = ('https://maps.googleapis.com/maps/api/place/nearbysearch/json'
			'?location=%s'
			'&radius=%s'
			'&types=restaurant'
			'&keyword=%s'
			'&key=%s'
			)% (LOCATION, RADIUS, KEYWORD[2:], AUTH_KEY)
	# print MyUrl
	response = urllib.urlopen(MyUrl)
	jsonRaw = response.read()
	jsonData = json.loads(jsonRaw)

	#parse place_id into list
	place_id_pool = []
	for place in jsonData['results']:
		place_id_pool.append(place['place_id'])
		# print place['place_id']
		# print place['name']
	return place_id_pool

if __name__ == '__main__':
	print google_nearby(-33.8670522, 151.1957362, 100, 'Chinese', 'Indian')	
  
