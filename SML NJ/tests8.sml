use "sampleTests8.sml";

local
  open Seq
in
fun test21 () = let val s = append([], N)
                in get 0 s = 0 andalso get 1776 s = 1776 end

fun test22 () = let fun f 0 = 42 | f _ = raise Fail "boom"
                  val s = map f N
                in
                  get 0 s = 42 andalso
                  ((get 10 s; false) handle Fail "boom" => true)
                end

fun test23 () = get 0 (filter (fn x => x < 1) N) = 0

fun test24 () = let val s = tabulate (fn x => tabulate (fn y => x))
                in get 2013 (hd s) = 0 andalso hd (get 2014 s) = 2014 end

fun test25 () = let val s = iter (fn x => () :: x) []
                in length (get 2014 s) = 2014 andalso get 1 s = [()] end

fun test26 () = let val s = iterList List.hd [1,2,3]
                in take (s, 10) = [1,2,3,1,2,3,1,2,3,1] end

fun test27 () = let fun f [x] = if x mod 2 = 0 then x div 2 else 3*x+1
                in get 106 (iterList f [31]) = 1 end

fun test28 () = get 2014 (repeat ["X"]) = "X"

fun test29 () = let val x = iter (fn _ => #"A") #"A"
                  val y = tabulate (fn _ => #"B")
                  val s = merge (x,y)
                in take (s, 10) = explode "ABABABABAB" end

fun test30 () = let fun m x = tabulate (fn _ => x)
                  val s = mergeList1 (List.tabulate(100, m))
                  val l = List.tabulate(2014, fn x => x mod 100)
                in take(s, 2014) = l end

fun test31 () = let fun f x = if x mod 2014 = 0 then [#"A",#"B",#"C"] else []
                  val s = mergeList2 (tabulate f)
                in take(s, 10) = explode "ABCABCABCA" end

fun test32 () = let fun f x = tabulate (fn y => (x,y))
                  val s = mergeSeq (tabulate f)
                  fun fdp s y = findPos (fn x : int * int => x=y) s
                  fun fdn s y = findNeg 1000 (fn x : int * int => x=y) s
                  val x = hd s
                  val y = hd (tl s)
                in fdp s (3,14) andalso
                   fdp s (0,0) andalso
                   fdn (tl s) x andalso
                   fdn (tl (tl s)) y
                end

fun test33 () = let val s = upTo 2014
                in take (drop (s, 2011), 5) = [2011, 2012, 2013, 2014, 2014] end

fun test34 () = take(drop (Primes, 1000), 10)
                = [7927,7933,7937,7949,7951,7963,7993,8009,8011,8017]

fun test35 () = let fun addyes () = cons ("yes", addno)
                    and addno () = cons ("no", addyes)
                in take (drop (addyes(), 2014), 2) = ["yes", "no"] end

fun test36 () = let fun add x = cons (x, fn () => add (x+1))
                in get 2014 (add 0) = 2014 end

end

val allTests = allSampleTests @ [
 (test21, "test21"),
        (test22, "test22"),
        ( test23, " test23"),
        ( test24, " test24"),
        ( test25, " test25"),
       (*) ( test26, " test26"),*)
        ( test27, " test27"),
        ( test28, " test28"),
        (test29, "test29"),
        (test30, "test30"),
        (test31, "test31"),
        (test32, "test32"),
        (test33, "test33"),
        (test34, "test34"),
        ( test35, " test35"),
        ( test36, " test36")
]
(*)
val OK = List.all (fn t => t()) allTests
*)
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