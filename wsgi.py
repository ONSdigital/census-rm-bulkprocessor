import os

from main import app

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=os.getenv('PORT', '5000'))
