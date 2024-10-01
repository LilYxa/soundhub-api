from uuid import UUID


class UserNotFoundException(Exception):
    def __init__(self, user_id: UUID):
        self.message = f'User with id {str(user_id)} was not found'
        super().__init__(self.message)