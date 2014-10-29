$out = "out-logs"
$currentPath = (Resolve-Path .)
$graphs = (gci -recurse .\data\graphLib\* -include *.dgf, *.dimacs |
  select-string "p edges? ([0-9]+) ([0-9]+)" | 
  % {
    new-object PSObject -Property @{
      "FileName"=(Resolve-Path -Relative $_.Path);
      "Nodes"=[int]$_.matches.groups[1].Value;
      "Edges"=[int]$_.matches.groups[2].Value 
    } 
  } |
  sort Nodes |
  where { $_.Nodes -gt 0 } )

$graphs | % { 
  $Path = (Join-Path $out (Split-Path $_.FileName)) 
  if(!(Test-Path -Path $Path)) {
    New-Item -ItemType Directory -Path $Path
  }
}

$graphs | % {
  $FileName = $_.FileName
  $LogName = (Join-Path $out $FileName) + ".txt"
  $Run = {
    param($currentPath, $FileName, $LogName)
    cd $currentPath
    ./scripts/runHeuristic.ps1 $FileName 2>&1 > $LogName
  }
  Write-Output "Starting job on $($_.FileName)"
  $job = Start-Job -ScriptBlock $Run -ArgumentList $currentPath,$FileName,$LogName
  Write-Output "Waiting for job $($job.Id)"
  Wait-Job -Timeout 3600 $job
  if ($job.State -eq "Running") {
    Write-Output "Timed out, stopping job forcibly"
    Stop-Job $job
  }
  Receive-Job $job
}
