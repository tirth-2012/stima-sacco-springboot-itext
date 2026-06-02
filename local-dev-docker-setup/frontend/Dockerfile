# Stage 1: Build React
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
# Use production environment
RUN npm run build -- --mode production

# Stage 2: export build
FROM alpine
WORKDIR /output
COPY --from=build /app/dist .