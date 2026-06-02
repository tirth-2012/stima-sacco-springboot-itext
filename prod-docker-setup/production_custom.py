from mayan.settings.production import *

ALLOWED_HOSTS = ["*"]

CSRF_TRUSTED_ORIGINS = [
    "https://localhost",
]

REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': [
        'rest_framework.authentication.SessionAuthentication',
        'rest_framework.authentication.TokenAuthentication',
    ],
}

CSRF_COOKIE_SECURE = False
SESSION_COOKIE_SECURE = False

# ✅ REQUIRED FOR SUBPATH DEPLOYMENT
FORCE_SCRIPT_NAME = "/mayan"
USE_X_FORWARDED_PREFIX = True

USE_X_FORWARDED_HOST = True
SECURE_PROXY_SSL_HEADER = ("HTTP_X_FORWARDED_PROTO", "https")

STATIC_URL = "/mayan/static/"
MEDIA_URL = "/mayan/media/"

LOGIN_URL = "/mayan/authentication/login/"