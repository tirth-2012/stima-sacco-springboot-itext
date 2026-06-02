Write-Host "Stopping containers and removing volumes..."
docker compose down -v

Write-Host "Removing dangling volumes..."
docker volume prune -f

Write-Host "Rebuilding and starting containers with .env..."
docker compose --env-file .env up -d --build

Write-Host "Waiting for Mayan to initialize..."
Start-Sleep -Seconds 10

Write-Host "Showing logs (look for 'Creating superuser')..."
docker logs -f mayan_app