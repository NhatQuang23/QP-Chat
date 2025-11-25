<#
Build script for QPMess (PowerShell)
Usage: Run this from the project folder or execute .\build.ps1
#>
$javac = "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\javac.exe"
if (-not (Test-Path $javac)) {
    Write-Host "javac not found at $javac. Update the path in this script." -ForegroundColor Yellow
}

Set-Location -Path $PSScriptRoot

Write-Host "Compiling Java sources..." -ForegroundColor Cyan

$commonArgs = @('-cp', '.;MongoDriver\mongo-java-driver-3.12.13.jar', '--module-path', 'FX SDK\lib', '--add-modules', 'javafx.controls,javafx.fxml')

# Compile packages in smaller groups to avoid command-line length limits
$groups = @('database', 'Collections', 'SessionManager', 'Views', 'network')
foreach ($g in $groups) {
    if (Test-Path $g) {
        $files = Get-ChildItem -Path $g -Recurse -Filter *.java | ForEach-Object { $_.FullName }
        if ($files.Count -gt 0) {
            Write-Host "Compiling $g ( $($files.Count) files )..."
            # Build a safe command line with quoted file paths to avoid PowerShell/javac parsing issues
            $quotedFiles = $files | ForEach-Object { '"' + $_ + '"' } | Out-String
            $quotedFiles = $quotedFiles -replace "\r?\n"," "; $quotedFiles = $quotedFiles.Trim()
            $cmd = '"' + $javac + '" -cp ".;MongoDriver\\mongo-java-driver-3.12.13.jar" --module-path "FX SDK\\lib" --add-modules javafx.controls,javafx.fxml ' + $quotedFiles
            Write-Host "Running: $cmd"
            cmd /c $cmd
            if ($LASTEXITCODE -ne 0) { Write-Error "Compilation failed in $g"; exit $LASTEXITCODE }
        }
    }
}

# Compile top-level UI classes
$top = @('ChatApp.java','Home.java','NewContactDialog.java','PrivacySettingsUI.java','UserRegistrationForm.java','AccountSettingsUI.java')
$existingTop = $top | Where-Object { Test-Path $_ }
if ($existingTop.Count -gt 0) {
    Write-Host "Compiling top-level classes..."
    $topFiles = $existingTop | ForEach-Object { '"' + (Resolve-Path $_).Path + '"' } | Out-String
    $topFiles = $topFiles -replace "\r?\n"," "; $topFiles = $topFiles.Trim()
    $cmdTop = '"' + $javac + '" -cp ".;MongoDriver\\mongo-java-driver-3.12.13.jar" --module-path "FX SDK\\lib" --add-modules javafx.controls,javafx.fxml ' + $topFiles
    Write-Host "Running: $cmdTop"
    cmd /c $cmdTop
    if ($LASTEXITCODE -ne 0) { Write-Error "Top-level compilation failed"; exit $LASTEXITCODE }
}

Write-Host "Build finished." -ForegroundColor Green
