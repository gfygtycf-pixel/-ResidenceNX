from flask import Flask, render_template, request, jsonify
import sqlite3
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
DB_PATH = BASE_DIR / "eldoria.db"

app = Flask(__name__)


# =========================
# ТОВАРЫ
# =========================

PRODUCTS = {

    # ---------- ПРИВИЛЕГИИ ----------

    "baron": {
        "name": "Барон",
        "price": 99,
        "type": "privilege",

        "description": (
            "Первая ступень к знатному титулу Элдории."
        ),

        "features": [
            "Цветной ник в чате",
            "Префикс [Барон]",
            "2 дополнительных региона",
            "3 точки дома",
            "Ежедневный /kit baron"
        ],

        "kit": [
            "Железная броня",
            "50 000 монет"
        ]
    },


    "duke": {
        "name": "Герцог",
        "price": 199,
        "type": "privilege",

        "description": (
            "Больше возможностей и ускоренное развитие."
        ),

        "features": [
            "Всё из привилегии Барон",
            "Префикс [Герцог]",
            "5 дополнительных регионов",
            "6 точек дома",
            "/repair",
            "/feed",
            "/enderchest",
            "Ежедневный /kit duke"
        ],

        "kit": [
            "Алмазная броня",
            "100 000 монет"
        ]
    },


    "king": {
        "name": "Король",
        "price": 349,
        "type": "privilege",

        "description": (
            "Высшая привилегия для настоящего правителя Элдории."
        ),

        "features": [
            "Всё из привилегии Герцог",
            "Префикс [Король]",
            "10 дополнительных регионов",
            "10 точек дома",
            "Максимальные лимиты привилегий",
            "Ежедневный /kit king"
        ],

        "kit": [
            "Зачарованная алмазная броня",
            "250 000 монет"
        ]
    },


    # ---------- ВАЛЮТА ----------

    "money_1m": {
        "name": "1 000 000 монет",
        "price": 50,
        "type": "money",

        "description": (
            "Отличный вариант для быстрого старта."
        ),

        "amount": 1_000_000,

        "features": [
            "1 000 000 игровой валюты"
        ]
    },


    "money_3m": {
        "name": "3 000 000 монет",
        "price": 120,
        "type": "money",

        "description": (
            "Больше монет — больше выгоды."
        ),

        "amount": 3_000_000,
        "discount": 20,

        "features": [
            "3 000 000 игровой валюты",
            "Выгода 20%"
        ]
    },


    "money_10m": {
        "name": "10 000 000 монет",
        "price": 300,
        "type": "money",

        "description": (
            "Максимальный пакет игровой валюты."
        ),

        "amount": 10_000_000,
        "discount": 40,

        "features": [
            "10 000 000 игровой валюты",
            "Выгода 40%"
        ]
    }
}


# =========================
# БАЗА ДАННЫХ
# =========================

def get_db():
    connection = sqlite3.connect(DB_PATH)

    connection.row_factory = sqlite3.Row

    return connection


def init_db():

    with get_db() as connection:

        connection.execute("""
            CREATE TABLE IF NOT EXISTS orders (

                id INTEGER PRIMARY KEY AUTOINCREMENT,

                nick TEXT NOT NULL,

                product TEXT NOT NULL,

                amount INTEGER NOT NULL,

                status TEXT NOT NULL DEFAULT 'test',

                created_at DATETIME
                    DEFAULT CURRENT_TIMESTAMP

            )
        """)

        connection.commit()


# =========================
# ГЛАВНАЯ
# =========================

@app.route("/")
def index():

    privileges = []

    for product_id in ["baron", "duke", "king"]:

        product = PRODUCTS[product_id].copy()

        product["id"] = product_id

        privileges.append(product)


    currency = []

    for product_id in [
        "money_1m",
        "money_3m",
        "money_10m"
    ]:

        product = PRODUCTS[product_id].copy()

        product["id"] = product_id

        currency.append(product)


    return render_template(
        "index.html",
        privileges=privileges,
        currency=currency
    )


# =========================
# СТРАНИЦА ТОВАРА
# =========================

@app.route("/product/<product_id>")
def product(product_id):

    if product_id not in PRODUCTS:

        return "Товар не найден", 404


    product = PRODUCTS[product_id].copy()

    product["id"] = product_id


    return render_template(
        "product.html",
        product=product
    )


# =========================
# ТЕСТОВЫЙ ЗАКАЗ
# =========================

@app.post("/api/test-order")
def test_order():

    data = request.get_json(
        silent=True
    ) or {}


    nick = (
        data.get("nick") or ""
    ).strip()


    product_id = data.get(
        "product"
    )


    # Проверяем ник

    if not nick:

        return jsonify({
            "ok": False,
            "error": "Укажи игровой ник."
        }), 400


    if len(nick) > 32:

        return jsonify({
            "ok": False,
            "error": "Слишком длинный ник."
        }), 400


    # Проверяем товар

    if (
        not product_id
        or product_id not in PRODUCTS
    ):

        return jsonify({
            "ok": False,
            "error": "Товар не найден."
        }), 400


    product = PRODUCTS[product_id]


    # Создаём заказ

    with get_db() as connection:

        cursor = connection.execute(

            """
            INSERT INTO orders
            (
                nick,
                product,
                amount,
                status
            )

            VALUES (?, ?, ?, ?)
            """,

            (
                nick,
                product_id,
                product["price"],
                "test"
            )
        )


        order_id = cursor.lastrowid

        connection.commit()


    return jsonify({

        "ok": True,

        "order_id": order_id,

        "message":
            "Тестовый заказ создан."
    })


# =========================
# АДМИНКА
# =========================

@app.route("/admin/orders")
def admin_orders():

    with get_db() as connection:

        orders = connection.execute(
            """
            SELECT *
            FROM orders
            ORDER BY id DESC
            """
        ).fetchall()


    return render_template(
        "admin.html",
        orders=orders
    )


# =========================
# ЗАПУСК
# =========================

if __name__ == "__main__":

    init_db()

    app.run(
        host="0.0.0.0",
        port=5000,
        debug=True
    )