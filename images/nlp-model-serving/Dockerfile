FROM python:3.7

RUN pip install fastapi==0.63.0 uvicorn==0.13.4 transformers==4.5.0 torch==1.8.1

EXPOSE 8002

COPY ./app /app

CMD ["uvicorn", "app.serve:app", "--host", "0.0.0.0", "--port", "8002"]