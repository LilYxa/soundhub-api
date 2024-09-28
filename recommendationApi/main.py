# for starting server execute in file directory: "uvicorn main:app --reload --host 0.0.0.0 --port 8888"
from uuid import UUID

from fastapi import FastAPI, HTTPException
from fastapi.responses import HTMLResponse

import recommend
from logger import logger

app = FastAPI()


@app.get("/", response_class=HTMLResponse)
def healthcheck():
    return "<h1>All good!</h1>"


@app.get("/recommend/{user_id}")
def recommend_users(user_id: UUID):
    logger.debug(f"recommend_users[1]: user_id = {user_id}")
    try:
        friend_list = recommend.main(user_id=user_id)
        return friend_list
    except Exception as e:
        logger.error(f"recommend_users[2]: {e}")
        raise HTTPException(404, f"{e}")
