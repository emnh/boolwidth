gci -recurse data\graphLib\* -include *.dgf, *.dimacs |
resolve-path -relative |
% { 
	write-output $_
	./scripts/runHeuristic.ps1 $_
}
