$out = "out-logs"
$lower = 271
$upper = 550
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
  sort -Desc Nodes | 
  where { $_.Nodes -ge $lower -and $_.Nodes -lt $upper })

$graphs | % {
  $FileName = $_.FileName
  $LogName = (Join-Path $out $FileName) + ".txt"
  if (!(Test-Path $LogName)) {
    Write-Output "not done: $FileName"
  } else {
    $timeline = & {
      gc $LogName | select-string '{ "time.*"BW":'
      gc $LogName | select-string '^time:'
      }
    if ($timeline.length -eq 0) {
      #Write-Output "failed graph: $FileName"
    } else {
      Write-Output $timeline | % { $_ -replace '\\','/' }
    }
  }
}
