$out = "out-logs"
$lower = $args[0]
$upper = $args[1]
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
  where { $_.Nodes -ge $lower -and $_.Nodes -lt $upper } )

$graphs | % { 
  $Path = (Join-Path $out (Split-Path $_.FileName)) 
  if(!(Test-Path -Path $Path)) {
    New-Item -ItemType Directory -Path $Path
  }
}

Write-Output "Starting run for graphs with nodes in range [$lower, $upper>"

$graphs | % {
  $FileName = $_.FileName
  $LogName = (Join-Path $out $FileName) + ".txt"
  $Run = {
    param($currentPath, $FileName, $LogName)
    cd $currentPath
    ./scripts/runHeuristic.ps1 $FileName 2>&1 > $LogName
  }
  Write-Output "Starting job on $($_.FileName) with $($_.Nodes) nodes"
  $job = Start-Job -ScriptBlock $Run -ArgumentList $currentPath,$FileName,$LogName
  Write-Output "Waiting for job $($job.Id)"
  Wait-Job -Timeout 3600 $job
  if ($job.State -eq "Running") {
    Write-Output "Timed out, stopping job forcibly"
    Stop-Job $job
  }
  Receive-Job $job
}
