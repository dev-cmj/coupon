import random
from locust import task, FastHttpUser


class CouponIssueV2(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def issue(self):
        payload = {
            "couponId": 1,
            "userId": random.randint(1, 10000000),
        }
        with self.rest("POST", "/v2/issue-async", json=payload):
            pass
