# for starting server execute in file directory: "uvicorn main:app --reload --host 0.0.0.0 --port 8888"

from uuid import UUID
import uuid
from fastapi import FastAPI, HTTPException
from fastapi.responses import HTMLResponse
import recommend

app = FastAPI()

@app.get("/", response_class=HTMLResponse)
def healthcheck():
    return "<h1>All good!</h1>"

@app.get("/recommend/{user}")
def usersgenres(user: UUID):
    try:
        friendsList = recommend.main(user_id=user)
        if type(friendsList) == type("str"):
            raise HTTPException(404, f"{friendsList}")
    except:
        raise HTTPException(404, f"{friendsList}")
    return friendsList