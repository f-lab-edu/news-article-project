import json
import pandas as pd

json_path = "../news_data/data/training/source_data/TS_span_extraction.json"

with open(json_path, "r", encoding="utf-8") as f:
    raw_data = json.load(f)

records = []
for article in raw_data["data"]:
    title = article.get("doc_title", "")
    category = article.get("doc_class", {}).get("code","")
    published_at = article.get("doc_published",None)

    for para in article.get("paragraphs", []):
        content = para.get("context","")
        if content:
            records.append({
                "title": title,
                "content": content,
                "category": category,
                "published_at": published_at
            })

df = pd.DataFrame(records)
df.to_csv("articles_from_aihub.csv", index=False, encoding='utf-8-sig')
print("CSV 저장 완료: articles_from_aihub.csv")
print(df.shape)
print(df.head(3))