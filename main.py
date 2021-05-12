import json
import os

from flask import Flask, flash, request, redirect, Response
from werkzeug.utils import secure_filename

app = Flask(__name__,
            static_url_path='',
            static_folder='ui/build')
app.secret_key = "super secret key"

ALLOWED_EXTENSIONS = ['csv']


@app.route('/processors')
def get_processor():
    return  {'processors': [
                      {'bulkType': 'Refusals', 'Desc': 'Refusals Case'},
                      {'bulkType': 'Uninvalidate', 'Desc': 'Uninvalidate Case' },
                      {'bulkType': 'New Address', 'Desc': 'New Addresses'},
                      {'bulkType': 'Address Modification', 'Desc': 'Address Modifications'},
                      {'bulkType': 'Invalidate', 'Desc': 'Invalidate Case'},
                      {'bulkType': 'Non Compliance', 'Desc': 'Non Compliance Cases'},
                      {'bulkType': 'DeactivateUAC', 'Desc': 'Deactivate UACs' },
                  ]}


@app.route('/')
def root():
    return app.send_static_file('index.html')


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # if user does not select file, browser also
        # submit an empty part without filename
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join('/tmp', filename))
            return Response('Got your file!')
    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <input type=file name=file>
      <input type=submit value=Upload>
    </form>
    '''
