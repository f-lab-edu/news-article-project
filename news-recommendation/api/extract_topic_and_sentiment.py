import pandas as pd
from konlpy.tag import Okt
from sklearn.feature_extraction.text import TfidfVectorizer
from transformers import AutoTokenizer, AutoModelForTokenClassification, pipeline
from transformers import pipeline as sentiment_pipeline_loader
import numpy as np
import re

# 1. 데이터 로드
df = pd.read_csv("articles_from_aihub.csv")
df_sample = df.head(1000).copy()

# 2. 형태소 분석기
okt = Okt()

# 3. 감정 키워드 사전
positive_keywords = [w.lower() for w in [
    "축하", "기쁨", "감동", "성공", "승리", "긍정", "훈훈", "좋은", "환영", "감사",
    "기대", "호응", "행복", "지원", "개선", "성과", "협력", "도움"
]]
negative_keywords = [w.lower() for w in [
    "사망", "논란", "비판", "실패", "폭행", "하락", "부정", "불행", "참사", "고통",
    "비극", "우려", "징계", "사건", "범죄", "위기", "분노", "불만"
]]

# 4. 파이프라인 로딩
ner_tokenizer = AutoTokenizer.from_pretrained("kykim/bert-kor-base")
ner_model = AutoModelForTokenClassification.from_pretrained("kykim/bert-kor-base")
ner_pipeline = pipeline("ner", model=ner_model, tokenizer=ner_tokenizer, aggregation_strategy="simple")

sentiment_pipeline = sentiment_pipeline_loader("sentiment-analysis", model="snunlp/KR-FinBert-SC")

# 5. TF-IDF Vectorizer
tfidf_vectorizer = TfidfVectorizer(max_features=1000)

# 6. 문장 분리기 (정규식 기반)
def split_korean_sentences(text):
    sentences = re.split(r'(?<=[.?!다])(?=\s)', text.strip())
    return [s.strip() for s in sentences if s.strip()]

# 7. TF-IDF 키워드 추출
def extract_keyword_tfidf(text: str, top_k: int = 1):
    try:
        nouns = okt.nouns(text)
        if not nouns:
            return None
        joined = ' '.join(nouns)
        tfidf_matrix = tfidf_vectorizer.fit_transform([joined])
        scores = tfidf_matrix.toarray()[0]
        indices = np.argsort(scores)[::-1][:top_k]
        return tfidf_vectorizer.get_feature_names_out()[indices[0]]
    except Exception as e:
        print("[TFIDF Error]", e)
        return None

# 8. NER 키워드 추출
def extract_keyword_ner(text: str):
    try:
        results = ner_pipeline(text[:512])
        valid_types = ['PER', 'ORG', 'LOC', 'MISC']
        return [r['word'] for r in results if r['entity_group'] in valid_types]
    except Exception as e:
        print("[NER Error]", e)
        return []

# 9. 주제 + 감정 통합 추출 함수
def extract_topic_and_sentiment(text: str, title: str = None):
    # 주제 추출
    ner_keywords_text = extract_keyword_ner(text)
    ner_keywords_title = extract_keyword_ner(title) if title else []

    if ner_keywords_title:
        topic = sorted(ner_keywords_title, key=len, reverse=True)[0]
    elif title and ner_keywords_text:
        topic = next((k for k in ner_keywords_text if k in title), None)
    elif ner_keywords_text:
        topic = sorted(ner_keywords_text, key=len, reverse=True)[0]
    else:
        topic = extract_keyword_tfidf(text)
        if not topic and title:
            topic = extract_keyword_tfidf(title)

    # 감정 분석
    sentiment = "neutral"
    try:
        sentences = split_korean_sentences(text)
        sentiment_counts = {"positive": 0, "neutral": 0, "negative": 0}

        for sent in sentences[:10]:
            result = sentiment_pipeline(sent[:512])[0]
            label = result["label"].lower()
            sentiment_counts[label] += 1

        lowered_text = text.lower()
        if any(word in lowered_text for word in positive_keywords):
            sentiment_counts["positive"] += 1
        if any(word in lowered_text for word in negative_keywords):
            sentiment_counts["negative"] += 1

        sentiment = max(sentiment_counts, key=sentiment_counts.get)

    except Exception as e:
        print("[Sentiment Error]", e)

    return pd.Series([topic, sentiment])

# 10. 분석 적용
df_sample[['predicted_topic', 'predicted_sentiment']] = df_sample.apply(
    lambda row: extract_topic_and_sentiment(row['content'], row['title']),
    axis=1
)

# 11. 저장
df_sample.to_csv("articles_sample_with_topic_and_sentiment.csv", index=False, encoding='utf-8-sig')
print("✅ 통합 주제 + 감정 분석 완료 및 저장: articles_sample_with_topic_and_sentiment.csv")
print(df_sample[['title', 'predicted_topic', 'predicted_sentiment']].head(10))