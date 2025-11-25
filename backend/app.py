import feedparser
import time
from functools import wraps
from flask import Flask, jsonify
from flask_cors import CORS
import requests
from datetime import datetime
import os

app = Flask(__name__)
CORS(app)  # 允许跨域请求

# 内存缓存
news_cache = {
    'data': None,
    'timestamp': 0
}
CACHE_DURATION = 24 * 60 * 60  # 24小时

def fetch_industrial_news():
    current_time = time.time()
    
    # 检查缓存是否有效
    if (news_cache['data'] is not None and 
        current_time - news_cache['timestamp'] < CACHE_DURATION):
        print("使用内存缓存数据")
        return news_cache['data']
    
    # 重新抓取数据
    print("重新抓取数据")
    rss_url = "https://news.google.com/rss/search?q=测温&hl=zh-CN&gl=CN&ceid=CN:zh-Hans"
    feed = feedparser.parse(rss_url)

    news = []
    for entry in feed.entries[:5]:
        news.append({
            "title": entry.title,
            "link": entry.link,
            "img": "https://images.unsplash.com/photo-1581090700227-1e37b190418e",
        })

    if not news:
        raise ValueError("No RSS news found")
    
    # 更新缓存
    news_cache['data'] = news
    news_cache['timestamp'] = current_time
    
    return news

def get_shenyang_weather(api_key):
    """
    获取沈阳当前天气信息
    """
    base_url = "http://api.openweathermap.org/data/2.5/weather"
    
    params = {
        "q": "Shenyang",
        "appid": api_key,
        "units": "metric",
        "lang": "zh_cn"
    }
    
    try:
        response = requests.get(base_url, params=params, timeout=10)
        
        if response.status_code == 200:
            return response.json()
        else:
            print(f"获取天气失败: {response.status_code}")
            return None
            
    except Exception as e:
        print(f"请求错误: {e}")
        return None

def get_weather_icon(icon_code):
    """
    获取天气图标表情
    """
    icon_map = {
        "01d": "☀️", "01n": "🌙",
        "02d": "⛅", "02n": "☁️",
        "03d": "☁️", "03n": "☁️",
        "04d": "☁️", "04n": "☁️",
        "09d": "🌧️", "09n": "🌧️",
        "10d": "🌦️", "10n": "🌦️",
        "11d": "⛈️", "11n": "⛈️",
        "13d": "❄️", "13n": "❄️",
        "50d": "🌫️", "50n": "🌫️",
    }
    return icon_map.get(icon_code, "🌈")

@app.route('/api/weather/shenyang', methods=['GET'])
def get_weather():
    """
    沈阳天气API接口
    """
    API_KEY = "d8fa0872d4519940b285ea84ab8386ad"
    # API_KEY = os.getenv("OPENWEATHER_API_KEY")

    # if not API_KEY:
    #     return jsonify({"error": "Weather API key not configured"}), 500

    weather_data = get_shenyang_weather(API_KEY)
    
    if weather_data:
        # 解析数据
        weather_info = weather_data.get("weather", [{}])[0]
        main_info = weather_data.get("main", {})
        
        description = weather_info.get("description", "未知")
        icon_code = weather_info.get("icon", "01d")
        temp = main_info.get("temp", "未知")
        humidity = main_info.get("humidity", "未知")
        pressure = main_info.get("pressure", "未知")
        
        icon_emoji = get_weather_icon(icon_code)
        update_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        
        # 返回JSON数据
        return jsonify({
            "city": "沈阳",
            "condition": description,
            "temperature": temp,
            "humidity": humidity,
            "pressure": pressure,
            "icon": icon_code,
            "icon_emoji": icon_emoji,
            "updateTime": update_time,
            "weather": weather_info,
            "main": main_info
        })
    else:
        return jsonify({
            "error": "获取天气信息失败"
        }), 500

@app.route("/api/news", methods=["GET"])
def get_news():
    data = fetch_industrial_news()
    return jsonify({"articles": data})

if __name__ == '__main__':
    app.run(debug=True, port=5000, host='127.0.0.1')