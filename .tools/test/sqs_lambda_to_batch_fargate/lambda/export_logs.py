import json
import logging
import os
import random

import boto3

s3_client = boto3.client("s3")
s3_resource = boto3.resource("s3")
logs_client = boto3.client("logs")

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

log_group_name = "/aws/batch/job"


def handler(event, context):
    logger.debug(f"BUCKET_NAME: {os.environ['BUCKET_NAME']}")
    logger.debug(f"INCOMING EVENT: {event}")

    status = event["detail"]["status"]

    if "Batch Job State Change" not in event["detail-type"]:
        logger.info(f"Non-triggering Batch event: {event['detail-type']}")
        return
    if "TIMED_OUT" in status:
        raise Exception(
            "Job timed out. Contact application owner or increase time out threshold"
        )
    if status not in ["FAILED", "SUCCEEDED"]:
        logger.info(f"Non-triggering Batch status: STATUS: {status}")
        return

    try:
        get_and_put_logs()
    except Exception as e:
        logger.error(json.dumps(f"Error: {str(e)}"))
        raise e


def get_and_put_logs():
    # Get most recent stream
    log_streams = logs_client.describe_log_streams(
        logGroupName=log_group_name,
        orderBy="LastEventTime",
        descending=True,
        limit=1,
    )

    # Get log events from the stream
    log_events = logs_client.get_log_events(
        logGroupName=log_group_name,
        logStreamName=log_streams["logStreams"][0]["logStreamName"],
        startFromHead=True,
    )

    log_file = "\n".join(
        [f"{e['timestamp']}, {e['message']}" for e in log_events["events"]]
    )
    file_identifier = str(random.randint(10**7, 10**8 - 1))

    s3_client.upload_fileobj(
        log_file,
        os.environ["PRODUCER_BUCKET_NAME"],
        f"{os.environ['LANGUAGE_NAME']}/{file_identifier}",
    )

    logger.info(
        f"Log data saved successfully: {os.environ['LANGUAGE_NAME']}/{file_identifier}"
    )
