from mayan.settings import *

ALLOWED_HOSTS = ["*"]

CSRF_TRUSTED_ORIGINS = [
    "https://localhost",
]

CSRF_COOKIE_SECURE = False
SESSION_COOKIE_SECURE = False

USE_X_FORWARDED_HOST = True

SECURE_PROXY_SSL_HEADER = ("HTTP_X_FORWARDED_PROTO", "https")

STATIC_URL = "/mayan/static/"
MEDIA_URL = "/mayan/media/"

LOGIN_URL = "/mayan/authentication/login/"
LOGIN_REDIRECT_URL = "/mayan/#/home/"

REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': [
        'rest_framework.authentication.SessionAuthentication',
        'rest_framework.authentication.TokenAuthentication',
    ],

    'DEFAULT_THROTTLE_CLASSES': [],

    'DEFAULT_THROTTLE_RATES': {}
}