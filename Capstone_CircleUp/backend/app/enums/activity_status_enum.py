from enum import Enum

class ActivityStatusEnum(str, Enum):
    OPEN = "OPEN"
    FULL = "FULL"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"