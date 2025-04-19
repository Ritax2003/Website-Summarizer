from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests
from bs4 import BeautifulSoup
import os
from mistralai import Mistral

app = FastAPI()

api_key = "BRrxKxh71OEFFBkAvxH4EtHqmK0kLhTA"
model = "mistral-large-latest"

client = Mistral(api_key=api_key)

def get_page_content(url: str):
    try:
        print(f"Fetching content from {url}")  # Log the URL being fetched
        response = requests.get(url)
        if response.status_code != 200:
            print(f"Failed to fetch URL: {url} - Status code: {response.status_code}")  # Log failure
            raise HTTPException(status_code=400, detail="Failed to fetch the URL.")

        soup = BeautifulSoup(response.content, 'html.parser')
        paragraphs = soup.find_all('p')

        if not paragraphs:
            print(f"No paragraphs found in content for URL: {url}")  # Log if no paragraphs are found

        content = " ".join([p.get_text() for p in paragraphs if p.get_text().strip() != ""])  # Ignore empty paragraphs
        if not content:
            print(f"No readable content found in paragraphs for URL: {url}")  # Log if no content found
            raise HTTPException(status_code=400, detail="No readable content found on the page.")

        return content
    except Exception as e:
        print(f"Error while fetching and parsing content for URL: {url}. Error: {str(e)}")  # Log error details
        raise HTTPException(status_code=500, detail=str(e))

class SummarizationRequest(BaseModel):
    url: str

@app.post("/summarize/")
async def summarize_url(request: SummarizationRequest):
    url = request.url.strip()  # Ensure no extra characters (e.g., newlines or spaces) are included in the URL
    print(f"Received request: {url}")  # Log received URL

    content = get_page_content(url)
    if not content:
        print("No content found on the page.")  # Log if no content is found
        raise HTTPException(status_code=400, detail="No content found on the page to summarize.")

    print(f"Page content extracted: {content[:500]}...")  # Log the first 500 characters of the content for debugging

    try:
        # Send a chat message to Mistral API for summarization
        stream_response = client.chat.stream(
            model=model,
            messages=[{
                "role": "user",
                "content": f"Summarize this content: {content}"
            }]
        )

        summary = ""
        for chunk in stream_response:
            summary += chunk.data.choices[0].delta.content

        if not summary:
            print("Mistral API did not return a summary.")  # Log if summary is empty
            raise HTTPException(status_code=500, detail="No summary returned from Mistral API.")

        return {"summary": summary}

    except Exception as e:
        print(f"Error in summarizing content: {str(e)}")  # Log the exception
        raise HTTPException(status_code=500, detail=f"Error summarizing content: {str(e)}")