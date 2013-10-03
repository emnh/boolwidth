// cd E:\privdata\dev\BoolWidth\FSharpTest\CutBoolExperiments\CutBoolExperiments\bin\Debug

namespace Experiments

module Graph =

    type IGraph<'a> = 
        abstract member getNodes : unit -> seq<'a>
        abstract member getNeighbors : 'a -> seq<'a>

    type Node<'a> = {
            Content : 'a;
            mutable Neighbors : Node<'a>[];
            mutable IsProcessed : bool;
    }

    let rec dfs (graph : IGraph<'a>) getNeighbors node seen =
        seq {
            yield node
            for neighbor in (getNeighbors node) do
                yield! dfs graph neighbor
        }

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

    let formatSet set = (String.concat " " (Seq.map (fun y -> y.ToString()) set))

    let formatTuple tuple =
        match tuple with 
            | (msg, inSet, outSet, set) ->
                sprintf "%s: in(%s) out(%s) set(%s)" msg (formatSet inSet) (formatSet outSet) (formatSet set)

    type BranchResult<'a> =
    | Count of int
    | HoodSeq of seq<'a>

    exception TypeMixBug of string

    //let a = if true then Count(2) else HoodSeq(seq { yield 3 })

    let connectedComponents neighborHoods = 
        let ar = Array.ofSeq neighborHoods
        let graph = Array.ofSeq seq {
            for h1 in neighborHoods do
                yield {
                    Content = h1; 
                    Neighbors = Array.ofSeq seq {
                        for h2 in neighborHoods do
                            if Set.intersect h1 h2 then yield h2
                    };
                    IsProcessed = False;
                }
        }
        graph
        //for (node, neighbors) in graph do    

    let countNeighborHoods neighborHoods countOnly = 
        let neighborHoods = Set.ofSeq neighborHoods
        let allVertices = (Set.unionMany neighborHoods)
        let wrapSeq = (if countOnly 
                        then fun aseq -> Count(Seq.length aseq)
                        else fun aseq -> HoodSeq(aseq))
        let rec branch inSet outSet rest restNeighborHoods = 
            //let restNeighborHoods = Set.map (fun x -> Set.intersect x rest) restNeighborHoods
            
            if (Set.isEmpty rest) then
                wrapSeq (seq [ ("fixed", inSet, outSet, inSet) ])
            elif (Set.isEmpty restNeighborHoods) then 
                wrapSeq Seq.empty
            elif (Set.count restNeighborHoods = 1) then
                wrapSeq (seq [ ("rest", inSet, outSet, Seq.head restNeighborHoods) ])
            else
                let selected = Seq.head rest
                //let containsInSet = (Set.filter (fun neighborHood -> Set.isSuperset neighborHood inSet) restNeighborHoods)
                let isValidIn = Set.isSubset (Set.add selected inSet) (Set.unionMany restNeighborHoods)
                let notContainsSelected = (Set.filter (fun neighborHood -> not (Set.contains selected neighborHood)) restNeighborHoods)
                let isValidOut = Set.isSubset inSet (Set.unionMany notContainsSelected)
                let inResult = 
                    (if isValidIn
                        then (branch 
                                (Set.add selected inSet)
                                outSet 
                                (Set.remove selected rest)
                                restNeighborHoods)
                        else wrapSeq Seq.empty)

                let outResult =
                    (if ((not (Set.isEmpty notContainsSelected)) && isValidOut)
                        then (branch 
                                inSet 
                                (Set.add selected outSet)
                                (Set.remove selected rest)
                                notContainsSelected)
                        else
                            wrapSeq Seq.empty)
                match (inResult, outResult) with
                    | Count(inLen), Count(outLen) -> Count(inLen + outLen)
                    | HoodSeq(inSeq), HoodSeq(outSeq) -> HoodSeq(seq { 
                                                            yield! inSeq
                                                            yield! outSeq
                                                        })
                    | _ -> raise (TypeMixBug("Cannot mix Count and HoodSeq"))

        let result = (branch Set.empty Set.empty allVertices neighborHoods) 
        match result with
            | Count(len) -> Count(len + 1)
            | HoodSeq(hseq) -> HoodSeq(seq {
                yield ("empty", Set.empty, Set.empty, Set.empty)
                yield! hseq
            })


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

    let printResults results =     
        (Seq.iter (fun x -> (printfn "%s" x)) results)

    let createTestNeighborHoods memberCount = 
        Seq.map 
            (fun x -> Set.ofSeq [ x; ])
            [1..memberCount]

    let testNeighborHoodsB = createTestNeighborHoods 16

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

    exception Bug of string
    let testCount2 power =
        let testNeighborHoods = createTestNeighborHoods power
        let count = match (ListNeighborHoods.countNeighborHoods testNeighborHoods true) with
                    | ListNeighborHoods.Count(len) -> len
                    | _ -> (raise (Bug ""))
        assert (count = (1 <<< power))
        count

    let y = ListNeighborHoods.countNeighborHoods testNeighborHoodsB true
    match y with
        | ListNeighborHoods.Count(len) -> printfn "y: %d" len
        | ListNeighborHoods.HoodSeq(y) -> printResults (Seq.map ListNeighborHoods.formatTuple y)
    
    //printResults (Seq.map ListNeighborHoods.formatSet results)

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
    showTiming (fun() -> (Test.testCount2 16)) "TestCount2"

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