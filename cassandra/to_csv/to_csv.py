import json
import csv
from html import escape

OUTPUT_REVIEW_PATH = "../../data/csv/reviews.csv"
REVIEW_PATH = "../../data/yelp_dataset/yelp_academic_dataset_review.json"

delimiter = "|"

def transform_review(doc):
    doc["review_text"] = doc.pop("text", None)
    doc["review_text"] = (doc["review_text"]
                          .replace("\r", "")
                          .replace("\\", "\\\\")
                          .replace("\n", "\\n")
                          )
    doc["review_text"] = doc["review_text"].replace(delimiter, f"\\{delimiter}")
    doc["review_text"] = escape(doc["review_text"])
    doc["date_reviewed"] = doc.pop("date", None).replace("  ", "T").replace(" ", "T")

    del doc["stars"]
    del doc["useful"]
    del doc["funny"]
    del doc["cool"]


keys = ['review_id', 'user_id', 'business_id', 'review_text', 'date_reviewed']

BATCH_SIZE = 100

with (open(REVIEW_PATH, "rt", encoding="utf-8") as input_data,
      # open(OUTPUT_REVIEW_PATH, "w", encoding="utf-8", newline="") as output_file
  ):
    # writer = csv.DictWriter(output_file, keys, delimiter=delimiter, escapechar=" ")
    # writer.writeheader()

    buffer = []
    for index, line in enumerate(input_data):
        # if len(buffer) >= BATCH_SIZE:
        #     writer.writerows(buffer)
        #     print(f"Parsed {index} lines!")
        #     buffer = []

        doc = json.loads(line)
        transform_review(doc)

        if doc.get('review_id', None) == "M1ZxAP_TxJ8NWh629UFEIA":
            print(line)
            print(doc)
        # buffer.append(doc)
