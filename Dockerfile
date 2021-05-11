FROM python:3.7-slim

EXPOSE 5000

RUN pip install pipenv

RUN groupadd --gid 1000 bulkprocessor && \
    useradd --create-home --system --uid 1000 --gid bulkprocessor bulkprocessor
WORKDIR /home/bulkprocessor
CMD ["./gunicorn_starter.sh"]

COPY Pipfile* /home/bulkprocessor/
RUN pipenv install --deploy --system
USER bulkprocessor

COPY --chown=bulkprocessor . /home/bulkprocessor
