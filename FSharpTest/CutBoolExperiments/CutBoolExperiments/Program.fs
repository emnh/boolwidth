// cd E:\privdata\dev\BoolWidth\FSharpTest\CutBoolExperiments\CutBoolExperiments\bin\Debug

namespace Experiments

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

    let countNeighborHoods neighborHoods = 
        let allVertices = (Set.unionMany neighborHoods)
        let branch inSet outSet rest restNeighborHoods = 
            let restNeighborhoods = Seq.map (fun x -> Set.intersect x rest) restNeighborHoods
            2
        branch Set.empty Set.empty allVertices neighborHoods


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
        assert (count = (1 <<< power))
        count
//        ((count = (1 <<< power)),
//            (Map.ofList 
//                ["function", "testCount";
//                ])
//            )

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
        let res = f()
        watch.Stop()
        watch.Elapsed.TotalMilliseconds
    
    let showTiming f msg =
        printfn "%s: Needed %f ms" msg (timeit f)
        
    showTiming (fun() -> (Test.testCount 16)) "TestCount"

    //printfn "%d" (Test.testCount 8)

open ListNeighborHoods

open Performance

//open NUnit.Framework
//
//[<TestFixture>]
//type myFixture() = class
//
//    [<Test>]
//    member self.myTest() =
//        Assert.AreEqual(1,1)
//        //test code
//
//end