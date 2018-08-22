import os

from flask import Flask
from flask_cors import CORS
from flask_restplus import Api, Resource

app = Flask(__name__)
api = Api(app)
cors = CORS(app)

@api.route('/hello')
class Hello(Resource):
    def get(self):
        try:
            return {'msg': 'hello world!'}
        except:
            return {'error': 'Oops!'}


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port='8080')
