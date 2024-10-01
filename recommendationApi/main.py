# for starting server execute in file directory: "uvicorn main:app --reload --host 0.0.0.0 --port 8888"
from fastapi import Depends, FastAPI, Request
from fastapi.responses import HTMLResponse, JSONResponse
from typing import Optional
from uuid import UUID

from exceptions.UserNotFoundException import UserNotFoundException
from services import RecommendationService
from utils import logger

app = FastAPI()


def get_service() -> Optional[RecommendationService]:
    return RecommendationService()

@app.exception_handler(UserNotFoundException)
async def user_not_found_exception_handler(request: Request, exc: UserNotFoundException):
    return JSONResponse(
        status_code=404,
        content={"message": exc.message},
    )

@app.exception_handler(ConnectionError)    
async def database_connection_exception_handler(request: Request, exc: ConnectionError):
    return JSONResponse(
        status_code=503,
        content={ "message": str(exc) }
    )
    

@app.get("/", response_class=HTMLResponse)
async  def healthcheck():
    return "<h1>All good!</h1>"


@app.get("/recommend/{user_id}")
async def recommend_users(user_id: UUID, service: Optional[RecommendationService] = Depends(get_service)):
    logger.debug(f"recommend_users[1]: user_id = {user_id}")
    friend_list = service.find_potential_friends(user_id) #recommend.main(user_id=user_id)
    
    logger.info(f'recommend_users[2]: {friend_list}')
    
    return friend_list
