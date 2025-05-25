FROM python:3.11


WORKDIR /usr/src/app

COPY requirements.txt .
RUN pip3 install -r requirements.txt

COPY queries.py .

CMD ["python3", "queries.py"] 
