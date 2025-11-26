#!/bin/bash
set -e

wait_for() {
  local url=$1
  echo "⏳ Waiting for $url ..."

  for i in {1..120}; do
    if curl -sf "$url" > /dev/null; then
      echo "✅ $url is UP"
      return 0
    fi
    sleep 1
  done

  echo "❌ Timeout waiting for $url"
  exit 1
}

wait_for "http://auth-service:8081/actuator/health"
wait_for "http://backend-keycloak-auth:8080/realms/ArmeniaBank"

echo "🚀 Starting Gateway..."
exec java -jar /app/app.jar
