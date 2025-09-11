$BlasterBot = @"
  ____  _           _              ____        _    __      ____   ___  
 |  _ \| |         | |            |  _ \      | |   \ \    / /_ | / _ \ 
 | |_) | | __ _ ___| |_ ___ _ __  | |_) | ___ | |_   \ \  / / | || | | |
 |  _ <| |/ _` / __| __/ _ \ '__| |  _ < / _ \| __|   \ \/ /  | || | | |
 | |_) | | (_| \__ \ ||  __/ |    | |_) | (_) | |_     \  /   | || |_| |
 |____/|_|\__,_|___/\__\___|_|    |____/ \___/ \__|     \/    |_(_)___/ 
                                                                        
"@

$zerotierNetworkId = "68bea79acf382249"
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

# check if user as zerotier installed
if (-not(Get-Command zerotier-cli -ErrorAction SilentlyContinue)) {
    Write-Host "Zerotier not found ❌"

    try {
        if ($isAdmin) {
            Write-Host "Please run this script without admin privileges to install Zerotier."
            exit 1
        }
        Write-Host "Installing Zerotier..."
        winget install --id Zerotier.ZerotierOne --silent
        Write-Host "Zerotier installed ✅"
    }
    catch {
        Write-Host "Failed to install Zerotier ❌"
        Write-Host "Error details: $_"
        exit 1
    }
}
else {
    if ($isAdmin) {
        Write-Host $BlasterBot
        Write-Host "`n- " -NoNewline
        Write-Host "ZeroTier found" -BackgroundColor DarkGreen
        Write-Host "- Using current ZeroTier installation..."
    }
}

# after installation, run as admin to join the network
# zerotier-cli requires admin privileges
if (-not $isAdmin) {
    Start-Process powershell -Verb runAs -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`""
    exit 0
}

# run the again script to join the network
if ($isAdmin) {

    $networks = zerotier-cli listnetworks
    $nodeID = zerotier-cli info | ForEach-Object { ($_ -split ' ')[2] }

    # check if already joined
    foreach ($line in $networks) {
        $fields = $line -split '\s+'
        $networkID = $fields[2]
        if ($networkID -eq $zerotierNetworkId) {
            $alreadyJoined = $true
            break
        }

        if ($alreadyJoined) {
            Write-Host "`nHere is your Node ID: $nodeID"
            exit 0
        }
    }

    Write-Host "- Joining Zerotier network...`n";
    $result = zerotier-cli join $zerotierNetworkId
    if ($result -match "200 join OK") {
        Write-Host "`nSuccessfully joined the Zerotier network!!!" -BackgroundColor DarkGreen

        $prefix = "#  Here is your Node ID: "
        $suffix = "  #"
        $contentLength = ($prefix + $nodeID + $suffix).Length
        $border = "#" * $contentLength
        $emptyLine = "#" + (" " * ($contentLength - 2)) + "#"


        Write-Host "`n$border"
        Write-Host $emptyLine
        Write-Host -NoNewline "$prefix"
        Write-Host -NoNewline $nodeID -ForegroundColor Cyan -BackgroundColor Black
        Write-Host $suffix
        Write-Host $emptyLine
        Write-Host $border
        Write-Host "`n"

    }
    else {
        Write-Host "Failed to join the Zerotier network ❌"
        Write-Host "Error details: $result"
        exit 1
    }

    Write-Host "Press Enter to exit..."
    Read-Host
}