// cd E:\privdata\dev\BoolWidth\FSharpTest\CutBoolExperiments\CutBoolExperiments\bin\Debug

module ListNeighborHoods =
    let private internalListNeighborHoods hoods newHood = 
        (Set.union
            hoods 
            (hoods |>
                Seq.map (fun x -> Set.union x newHood) |>
                Set.ofSeq))

    let listNeighborHoods initialHoods = 
        (Seq.fold internalListNeighborHoods (Set.ofSeq [ Set.empty ]) initialHoods)

//let results = testNeighborHoodsA
//let results2 = internalListNeighborHoods testNeighborHoodsA testNeighborHoodsA.[0]

module Test =
    let testNeighborHoodsA = 
        Seq.map 
            Set.ofSeq 
            [
                [ 1; 2; 5 ]
                [ 1; 2; 3; ]
                [ 4; ]
                [ 5; ]
            ]

    
    let createTestNeighborHoods memberCount = 
        Seq.map 
            (fun x -> Set.ofSeq [ x; ])
            [1..memberCount]

    let results = ListNeighborHoods.listNeighborHoods testNeighborHoodsA

    let testCount power =
        let testNeighborHoods = createTestNeighborHoods power
        let count = 
            Seq.length 
                (ListNeighborHoods.listNeighborHoods testNeighborHoods)
        assert (count = (1 <<< power) + 1)
        ((count = (1 <<< power) + 1),
            (Map.ofList 
                ["function", "testCount";
                ])
            )

    let printResults results =     
        Seq.iter (
            fun x -> (
                     printfn "%s" (
                        String.concat " " (
                            Seq.map (fun y -> y.ToString()) x))))
            results

    let run() =
        [
            testCount 8
        ]

    let runResults = run()

    let x = List.head runResults


//    let run() =
//        assert
//        (internalListNeighborHoods testNeighborHoodsA testNeighborHoodsA.[0]) ==)

module Performance =
    let timeit f = 
        let watch = new System.Diagnostics.Stopwatch()
        watch.Start()
        let res = f
        watch.Stop()
        watch.Elapsed.TotalMilliseconds
    
    let showTiming f msg =
        printfn "%s: Needed %f ms" msg (timeit f)
        
    showTiming (fun() -> 2) "Test"

open ListNeighborHoods

open NUnit.Framework

[<TestFixture>]
type myFixture() = class

    [<Test>]
    member self.myTest() =
        Assert.AreEqual(1,1)
        //test code

end