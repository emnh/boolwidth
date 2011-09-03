// Learn more about F# at http://fsharp.net


let testNeighborHoodsA = 
    Array.ofSeq [ 
        Set.ofSeq [ 1; 2; 5 ]
        Set.ofSeq [ 1; 2; 3; ]
        Set.ofSeq [ 4; ]
        Set.ofSeq [ 5; ]
        ]


let hoods = testNeighborHoodsA

let results = 
    testNeighborHoodsA |>
    Seq.map (fun x -> Set.union x testNeighborHoodsA.[0]) |>
    Set.ofSeq

Seq.iter (
        fun x -> (
                 printfn "%s" (
                    String.concat " " (
                        Seq.map (fun y -> y.ToString()) x))))
    results // testNeighborHoodsA.[0]

