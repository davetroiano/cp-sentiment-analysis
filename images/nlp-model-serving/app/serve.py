from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline

app = FastAPI()
sentiment_pipeline = pipeline('sentiment-analysis')

class Input(BaseModel):
    text: str

@app.post("/sentiment/")
async def sentiment(input: Input):
    sentiment = sentiment_pipeline(input.text)
    print(sentiment)
    return {"sentiment": sentiment[0]['label'].lower()}
