import os

from flask import Flask, flash, request, redirect, Response, jsonify
from werkzeug.utils import secure_filename

app = Flask(__name__,
            static_url_path='',
            static_folder='ui/build')
app.secret_key = "super secret key"

ALLOWED_EXTENSIONS = ['csv']


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


@app.route('/job', methods=['GET'])
def get_jobs():
    jobs = [{"id": "19864513-eca0-4d34-860e-4b71d9fc03af", "bulkProcess": "NEW_ADDRESS",
             "createdAt": "2021-05-17T11:32:10.106716+01:00", "lastUpdatedAt": "2021-05-17T11:32:11.631205+01:00",
             "fileName": "1_per_treatment_code.csv", "jobStatus": "FILE_UPLOADED", "rowCount": 47, "rowErrorCount": 0,
             "fatalErrorDescription": None},
            {"id": "8f0fde6c-d7c1-4eb0-a491-c79f993aad69", "bulkProcess": "NEW_ADDRESS",
             "createdAt": "2021-05-17T11:32:18.825215+01:00", "lastUpdatedAt": "2021-05-17T11:32:19.953604+01:00",
             "fileName": "goihdsfgdfslgndf.csv", "jobStatus": "PROCESSED_OK", "rowCount": 47, "rowErrorCount": 0,
             "fatalErrorDescription": None},
            {"id": "8f0fde6c-d7c1-4eb0-a491-c79f993aad69", "bulkProcess": "NEW_ADDRESS",
             "createdAt": "2021-05-17T12:12:12.123+01:00", "lastUpdatedAt": "2021-05-17T11:32:19.953604+01:00",
             "fileName": "ertoerhtgrg.csv", "jobStatus": "PROCESSED_TOTAL_FAILURE", "rowCount": 47, "rowErrorCount": 0,
             "fatalErrorDescription": None}]
    return jsonify(jobs)
