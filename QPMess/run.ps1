<#
Run script for QPMess (PowerShell)
Usage: Run from project folder: .\run.ps1
#>
$java = "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe"
if (-not (Test-Path $java)) {
    Write-Host "java not found at $java. Update the path in this script." -ForegroundColor Yellow
}

Set-Location -Path $PSScriptRoot

Write-Host "Starting QPMess (Home) ..." -ForegroundColor Cyan
& $java -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" --module-path "FX SDK\lib" --add-modules javafx.controls,javafx.fxml Home
