(* $Id: sampleTests8.sml 303 2014-04-25 02:18:00Z cs671a $ *)
(* Sample tests *)
use "8.sml";

local
  open Seq
in

val N = Naturals

fun get 0 s = hd s
  | get n s = get (n-1) (tl s)



fun findPos p =
    let fun search s = p (hd s) orelse search (tl s)
    in
      search
    end

fun findNeg n p s =
    case find n p s of
      NONE => true
    | _ => false

fun test01 () = get 2014 N = 2014
fun test02 () = take (N, 10) = [0,1,2,3,4,5,6,7,8,9]
fun test03 () = get 2014 (drop (N, 10)) = 2024
fun test04 () = let val s = append ([1,2,3,4,5], N)
                in get 2 s = 3 andalso get 2014 s = 2009 end
fun test05 () = get 2014 (map (fn x => [x]) N) = [2014]
fun test06 () = get 2014 (filter (fn x => x mod 3 = 0) N) = 2014 * 3
fun test07 () = find 10000 (fn x => x > 2014) N = SOME 2015 andalso
                find 10000 (fn x => x * x = 2014) N = NONE
fun test08 () = get 2014 (tabulate (fn x => 3*x+1)) = 6043
fun test09 () = let fun f x = if x mod 2 = 0 then x div 2 else 3*x+1
                in get 106 (iter f 31) = 1 end
fun test10 () = get 38 (iterList (fn [x,y] => x+y) [0,1]) = 39088169
fun test11 () = get 2014 (repeat [0,0,0,0,1,0]) = 1
fun test12 () = get 2014 (merge(N, N)) = 1007
fun test13 () = get 2014 (mergeList1 [N,N,N,N,N]) = 402
fun test14 () = let val s = mergeList2 (tabulate
                              (fn x => List.tabulate (x, fn _ => x)))
                in get 2015 s + get 2016 s = 127 end

local
  fun f n x = List.tabulate (n, fn _ => x)
  fun g n = tabulate (f (n+1))

  fun add x [] = [x:int list]
    | add x (y :: l) = if x = y then l else y :: add x l

  fun allDiff _ _ _ 0 = true
    | allDiff s l c n = let val l' = add (hd s) l val c' = c+1
                        in length l' = c' andalso allDiff (tl s) l' c' (n-1) end

  fun fdp s y = findPos (fn x : int list => x=y) s

  fun fdn s y = findNeg 1000 (fn x : int list => x=y) s
in
fun test15 () = let val s = mergeSeq (tabulate g)
                in fdp s [7,7,7] andalso
                   fdp s [3,3,3,3,3] andalso
                   fdn s [1,2,3] andalso
                   allDiff s [] 0 1000
                end
end
fun test16 () = get 2014 Primes = 17509
fun test18 () = (repeat []; false) handle List.Empty => true

local
val small = filter (fn x => x=2) N
val empty = filter (fn x => x<0) N
fun loop x = loop x (* a nonterminating function *)
in
fun term01 () = (hd small;true)
fun term02 () = (tl small;true)
fun term03 () = (tl (tl small);true)
fun term04 () = (tl empty;true)
fun term05 () = (take (small, 1);true)
fun term06 () = (take (empty, 0);true)
fun term07 () = (tl (drop (empty, 1000));true)
fun term08 () = (tl (mergeList2 (map (fn x => [x]) empty));true)
fun term09 () = (hd (mergeList2 (map (fn x => [x]) small));true)
fun term10 () = (hd (mergeList1 [small, empty, empty]); true)
fun term11 () = (tl (mergeList1 [empty, empty, empty]); true)
fun term12 () = (take (append ([1,2,3,4,5], small), 6);true)
fun term13 () = (tl (map loop empty);true)
fun term14 () = (tl (tabulate loop);true)
fun term15 () = (tl (drop (tabulate loop, 1000));true)
fun term16 () = (tl (iter loop ());true)
fun term17 () = (tl (iterList loop [1,2,3]);true)
fun term18 () = (tl (map loop N);true)
fun term19 () = (tl (filter loop N);true)
fun term20 () = (find 1000 (fn x => x>0) small;true)
fun term21 () = (hd (filter (fn x => x>0) small);true)
fun term22 () = (tl (filter (fn _ => false) empty);true)
fun term23 () = (tl (filter (fn _ => false) N);true)
fun term24 () = (hd (merge (small, empty));true)
fun term25 () = (tl (merge (empty, small));true)
fun term26 () = (tl (merge (empty, empty));true)
fun term27 () = (hd (tl (hd (tl (hd (tl
             (map (fn s => map (fn _ => N) s) (tabulate (fn _ => N))))))));true)
end

end

val allSampleTests = [
  (test01, "test01"),
        (test02, "test02"),
        ( test03, " test03"),
        ( test04, " test04"),
        ( test05, " test05"),
        ( test06, " test06"),
        ( test07, " test07"),
        ( test08, " test08"),
        (test09, "test09"),
        (test10, "test10"),
        (test11, "test11"),
        (test12, "test12"),
        (test13, "test13"),
        (test14, "test14"),
        ( test15, " test15"),
        ( test16, " test16"),

        ( test18, " test18"),
        (term01, "term01"),
        (term02, "term02"),
        ( term03, " term03"),
        ( term04, " term04"),
        ( term05, " term05"),
        ( term06, " term06"),
        ( term07, " term07"),
        ( term08, " term08"),
        (term09, "term09"),
        (term10, "term10"),
        (term11, "term11"),
        (term12, "term12"),
        (term13, "term13"),
        (term14, "term14"),
        ( term15, " term15"),
        ( term16, " term16"),
        ( term17, " term17"),
        ( term18, " term18"),
        ( term19, " term19"),
        ( term20, " term20"),
        (term21, "term21"),
        (term22, "term22"),
        (term23, "term23"),
        (term24, "term24"),
        (term25, "term25"),
        (term26, "term26"),
        (term27, "term27")
]
(*)
local
    fun add ((t,_),a) = (if t() then a+1 else a) handle _ => a
    val total = Int.toString (length allTests)
in
fun myScore () = Int.toString (foldl add 0 allTests) ^ "/" ^ total
end

local
    fun printBool true = print " === PASS ===\n"
      | printBool false = print " === FAIL ===\n"
in
fun runTest i =
    let
        fun get i = List.nth (allTests, i)
        val (t, name) = get i
    in
        print "\n";
        print name;
        printBool (t())
        handle _ => print " === FAIL (exception) ===\n"
    end
end
local
    fun printBool true = print " === PASS ===\n"
      | printBool false = print " === FAIL ===\n"
    fun run1 (t, name) = (
        print name;
        printBool (t())
        handle _ => print " === FAIL (exception) ===\n")
in
fun run () = List.app run1 allTests
end
*)