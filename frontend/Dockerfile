# =============================================================================
# STAGE 1: BUILD STAGE
# =============================================================================
# Use the official Node.js 20 image on Alpine Linux as the build environment.
# Node.js is needed to run npm and Vite (the build tool for the React app).
# "alpine" keeps the image small — only used for building, not for serving.
# We name this stage "builder" so we can copy from it in the next stage.
FROM node:20-alpine AS builder

# Set the working directory inside the container.
# All file operations and commands below will execute relative to /app.
WORKDIR /app

# ── Dependency caching trick ──────────────────────────────────────────────────
# Copy ONLY package.json and package-lock.json first.
# Docker caches this layer; if these files haven't changed, "npm ci" is skipped
# on the next build — saving minutes of downloading node_modules.
COPY package.json package-lock.json ./

# Install exact dependency versions from package-lock.json.
#   - Uses pnpm-lock.json (reproducible, deterministic installs)
#   - Fails if package-lock.json is out of sync with package.json
#   - Does NOT update package-lock.json
#   - Removes node_modules before installing (clean slate)
RUN npm install -g pnpm && pnpm install --frozen-lockfile

# Now copy the rest of the source code.
# Placed AFTER npm ci so Docker only re-installs packages when
# package files change, not on every source-code edit.
COPY . .

# Build the production-optimized React application using Vite.
# Output lands in the /app/dist folder as static HTML, CSS, and JS files.
# Vite performs tree-shaking, code splitting, and minification automatically.
RUN pnpm build


# =============================================================================
# STAGE 2: RUNTIME STAGE
# =============================================================================
# Use Nginx on Alpine Linux to serve the built static files.
# Nginx is a high-performance, battle-tested web server.
# "nginx:alpine" is only ~25 MB — perfect for serving static assets.
# Nothing from the Node.js build stage (node_modules, source) is included.
FROM nginx:alpine

# ── Security: non-root user ───────────────────────────────────────────────────
# By default Nginx master process runs as root to bind to port 80.
# We configure it to run the worker processes as the built-in "nginx" user.
# The master process still needs root briefly to bind to the port, but workers
# that handle actual requests run as the unprivileged "nginx" user.

# Copy the custom Nginx configuration file from our project into the container.
# This replaces the default Nginx config with our SPA-friendly configuration.
# nginx.conf handles: SPA routing, gzip compression, caching, security headers.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the compiled React app from the builder stage into Nginx's web root.
# "--from=builder" pulls files from the "builder" stage.
# "/usr/share/nginx/html" is Nginx's default directory for serving files.
# Everything else (node_modules, source code, Vite cache) is NOT copied.
COPY --from=builder /app/dist /usr/share/nginx/html

# ── Environment variable injection ────────────────────────────────────────────
# React/Vite bakes environment variables into the bundle at BUILD time.
# This script rewrites window.__ENV__ in index.html at CONTAINER START time,
# allowing different API URLs per environment (dev/staging/prod) without
# rebuilding the image.
COPY env.sh /docker-entrypoint.d/40-env.sh
RUN chmod +x /docker-entrypoint.d/40-env.sh

# Document that the container listens on port 80 (standard HTTP).
# This is metadata only — actual port mapping is done in docker-compose.
EXPOSE 80

# ── Health check ──────────────────────────────────────────────────────────────
# Verify Nginx is serving content every 30 seconds.
# wget -qO- fetches the page and discards output ("-q" quiet, "-O-" to stdout).
# Returns exit code 0 (healthy) if Nginx responds, 1 (unhealthy) if not.
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost:80/ || exit 1

# Nginx is started by the default entrypoint defined in the base nginx:alpine image.
# We don't need to define CMD — the base image already runs "nginx -g 'daemon off;'"
# which starts Nginx in the foreground (required for Docker to track the process).
