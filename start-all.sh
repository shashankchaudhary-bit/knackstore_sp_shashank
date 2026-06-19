#!/bin/bash
# KnackStore — Start Backend + Frontend together
# Requires: Java 17+ and Node.js 18+

set -u
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo ""
echo "======================================"
echo "  KnackStore — Starting Full Stack"
echo "======================================"
echo ""

# --- Check Java ---
if ! command -v java &> /dev/null; then
  echo "ERROR: Java not found. Install Java 17+ from https://adoptium.net"
  exit 1
fi
JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VER" -lt 17 ] 2>/dev/null; then
  echo "ERROR: Java 17+ required. You have Java $JAVA_VER."
  echo "Download from: https://adoptium.net"
  exit 1
fi

# --- Check Node ---
if ! command -v node &> /dev/null; then
  echo "ERROR: Node.js not found. Install Node 18+ from https://nodejs.org"
  exit 1
fi
NODE_VER=$(node -e "process.stdout.write(process.versions.node.split('.')[0])")
if [ "$NODE_VER" -lt 18 ] 2>/dev/null; then
  echo "ERROR: Node 18+ required. You have Node $NODE_VER."
  echo "Download from: https://nodejs.org"
  exit 1
fi

# --- Install frontend deps if needed ---
if [ ! -d "$ROOT_DIR/frontend/node_modules" ]; then
  echo "Installing frontend dependencies (first-time only — takes ~2 minutes)..."
  (cd "$ROOT_DIR/frontend" && npm install)
fi

BACKEND_PID=""
FRONTEND_PID=""

cleanup() {
  echo ""
  echo "Shutting down..."
  [ -n "$FRONTEND_PID" ] && kill "$FRONTEND_PID" 2>/dev/null
  [ -n "$BACKEND_PID" ] && kill "$BACKEND_PID" 2>/dev/null
  wait 2>/dev/null
  echo "Stopped."
  exit 0
}
trap cleanup INT TERM

# --- Start backend ---
echo "Starting Spring Boot backend on http://localhost:8080 ..."
(cd "$ROOT_DIR/backend" && ./mvnw spring-boot:run) &
BACKEND_PID=$!

# --- Start frontend ---
echo "Starting Angular frontend on http://localhost:4200 ..."
(cd "$ROOT_DIR/frontend" && npm start) &
FRONTEND_PID=$!

echo ""
echo "======================================"
echo "  Backend:  http://localhost:8080"
echo "  Swagger:  http://localhost:8080/swagger-ui.html"
echo "  H2 DB:    http://localhost:8080/h2-console"
echo "  Frontend: http://localhost:4200"
echo ""
echo "  Demo login: demo@knack.com / Demo@1234"
echo "======================================"
echo ""
echo "Press Ctrl+C to stop both servers."
echo ""

# Wait for either process to exit, then clean up the other
wait -n 2>/dev/null || wait
cleanup