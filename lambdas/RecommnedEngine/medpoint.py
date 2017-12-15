import json, requests

class ElasticsearchWrapper:
    def __init__(self):
        self.end_point = 'search-meethalf-embwd75mrizrbfo62bqi4hgv6m.us-east-1.es.amazonaws.com'
        self.index = 'geopoint'
        self.mapping_type = 'my_type'
        self.address = 'http://%s/%s/%s' % (self.end_point, self.index, self.mapping_type)
        self.create_index()
        self.id = 1

    def create_index(self):
        # Should be called at the first (in constructor)
        query = {
            "settings": {
                "number_of_shards": 2,
                "number_of_replicas": 1
            },
            "mappings": {
                self.mapping_type: {
                    "properties": {
                        "location": {"type": "geo_point"}
                    }
                }
            }
        }
        address = 'http://%s/%s' % (self.end_point, self.index) # has to be this!!
        response = requests.put(address, data=json.dumps(query)) # has to use put  
        print response

    def upload(self, data):
        print data
        upload_address = '%s/%d' % (self.address, self.id)
        self.id += 1
        response = requests.put(upload_address, data=data)
        print response

    def search_centroid(self):
        search_address = '%s/_search?size=0' % (self.address)
        query = {
            "aggs" : {
                "centroid" : {
                    "geo_centroid" : {
                        "field" : "location" 
                    }
                }
            }
        }
        response = requests.post(search_address, data=json.dumps(query))
        return response.json()

    def delete_index(self):
        delete_address = 'http://%s/%s' % (self.end_point, self.index)
        response = requests.delete(delete_address)
        print response


