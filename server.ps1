# Apna Store - Native Windows HTTP Web Server
$port = 3000
$prefix = "http://localhost:$port/"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Look for frontend folder, fallback to current dir
$rootPath = Join-Path $scriptDir "frontend"
if (-not (Test-Path $rootPath)) {
    $rootPath = $scriptDir
}

$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add($prefix)

try {
    $listener.Start()
    Write-Host "=================================================" -ForegroundColor Cyan
    Write-Host " Apna Store PowerShell HTTP Server Started!      " -ForegroundColor Yellow
    Write-Host " Serving Files From : $rootPath                  " -ForegroundColor Gray
    Write-Host " Store Frontend URL : $prefix                    " -ForegroundColor Green
    Write-Host "=================================================" -ForegroundColor Cyan
} catch {
    Write-Host "Could not start server on port $port : $_" -ForegroundColor Red
    exit 1
}

while ($listener.IsListening) {
    try {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response

        $relPath = $request.Url.LocalPath.TrimStart('/')
        if ([string]::IsNullOrWhiteSpace($relPath)) {
            $relPath = "index.html"
        }

        $localFilePath = Join-Path $rootPath $relPath

        if (Test-Path $localFilePath -PathType Leaf) {
            $bytes = [System.IO.File]::ReadAllBytes($localFilePath)
            $ext = [System.IO.Path]::GetExtension($localFilePath).ToLower()

            switch ($ext) {
                ".html" { $response.ContentType = "text/html; charset=utf-8" }
                ".css"  { $response.ContentType = "text/css; charset=utf-8" }
                ".js"   { $response.ContentType = "application/javascript; charset=utf-8" }
                ".png"  { $response.ContentType = "image/png" }
                ".jpg"  { $response.ContentType = "image/jpeg" }
                ".jpeg" { $response.ContentType = "image/jpeg" }
                ".svg"  { $response.ContentType = "image/svg+xml" }
                ".json" { $response.ContentType = "application/json; charset=utf-8" }
                default { $response.ContentType = "application/octet-stream" }
            }

            $response.ContentLength64 = $bytes.Length
            $response.OutputStream.Write($bytes, 0, $bytes.Length)
        } else {
            $response.StatusCode = 404
            $buffer = [System.Text.Encoding]::UTF8.GetBytes("<h1>404 Not Found</h1>")
            $response.ContentLength64 = $buffer.Length
            $response.OutputStream.Write($buffer, 0, $buffer.Length)
        }
        $response.Close()
    } catch {
        # continue loop safely
    }
}