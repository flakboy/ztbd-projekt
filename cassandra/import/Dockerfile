FROM python:3.11


WORKDIR /usr/src/app

COPY requirements.txt .
RUN pip3 install -r requirements.txt

COPY import.py .

CMD ["python3", "import.py"] 
