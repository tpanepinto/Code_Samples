use "7.sml";
use "pairPropStub.sml";

functor SampleTests
        (val name : string
         structure S : BOOL_STATE and P : PROP
         sharing type S.state = P.state) :
        sig val all : ((unit -> bool) * string * int) list end = 
struct

local
    val s = ref ""
    fun pr x = s := !s ^ x
    val print = SMLofNJ.Internals.prHook
    val truePrint = !print
in
fun getString f x =
    (s := "";
     print := pr;
     let val y = f x
     in
         print := truePrint;
         (y, !s)
     end handle e => (print := truePrint; raise e))
end

(* All BoolListState and BoolPairState Tests *)

local
    open S (* BoolListState or BoolPairState *)
    val s = set (set (set blankState ("a",false)) ("b",false)) ("a", true)
in
(* 1 *) fun test01 () = getString dumpState blankState = ((), "")
(* 1 *) fun test02 () = getString dumpState s = ((), "a = true\nb = false\n")
(* 1 *) fun test03 () = valOf (get s "a") : bool
(* 1 *) fun test04 () = not (valOf (get s "b"))
(* 1 *) fun test05 () = not (isSome (get s "c"))
(* 2 *) fun test06 () = not (isSome (get (unset s "a") "a"))
(* 2 *) fun test07 () = not (isSome (get (unset s "b") "b"))
(* 2 *) fun test08 () = not (isSome (get (unset s "c") "c"))
end

(* ListProp and PairProp *)

local
    open S (* BoolListState or BoolPairState *)
    open P (* ListProp or PairProp *)
    val p = Implies (Or (Ident "A",F),And (Not (Ident "B"),Ident "A"))
    val s = set blankState ("A",true)
in
(* 1 *) fun test09 () = p = parse "A|False->~B&A"
(* 1 *) fun test10 () = p = parse "A | False -> ~B & A"
(* 1 *) fun test11 () = p <> parse "A -> ~B & A"
(* 1 *) fun test12 () = identifiers p = ["A","B"]
(* 1 *) fun test13 () = isSome (satisfy p)
(* 1 *) fun test14 () = case satisfy (And (p, Ident "B")) of
                 NONE => false
               | SOME s => valOf (get s "B") andalso not (valOf (get s "A"))
(* 2 *) fun test15 () = getString isValid p = (false, "A = true\nB = true\n")
(* 2 *) fun test16 () = isValid (Implies (Not (Ident "A"), p))
(* 1 *) fun test17 () = not (isSome (satisfy
                                         (And (p, And (Ident "A", Ident "B")))))
(* 1 *) fun test18 () = case eval s p of
                 Unknown => true
               | _ => false
(* 1 *) fun test19 () = case eval (set s ("B", true)) p of
                 False => true
               | _ => false
(* 1 *) fun test20 () = case eval (set s ("B", false)) p of
                 True => true
               | _ => false
end

val all = List.map (fn (t, s, v) => (t, s^"["^name^"]", v)) [
        (test01, "test01", 1),
        (test02, "test02", 1),
        (test03, "test03", 1),
        (test04, "test04", 1),
        (test05, "test05", 1),
        (test06, "test06", 1),
        (test07, "test07", 1),
        (test08, "test08", 1),
        (test09, "test09", 1),
        (test10, "test10", 1),
        (test11, "test11", 1),
        (test12, "test12", 1),
        (test13, "test13", 1),
        (test14, "test14", 1),
        (test15, "test15", 1),
        (test16, "test16", 1),
        (test17, "test17", 1),
        (test18, "test18", 1),
        (test19, "test19", 1),
        (test20, "test20", 1)
    ]
end
structure T1 = SampleTests(val name = "pair"
                     structure S = BoolPairState and P = PairProp)
structure T2 = SampleTests(val name = "list"
                     structure S = BoolListState and P = ListProp)

val allTests = T1.all @ T2.all

local
    fun exec t = t() handle _ => false
    fun f ((t, _, v), (p, a)) = (if exec t then p+v else p, a+v)
in
fun myScore () =
    let val (p, a) = foldl f (0, 0) allTests
    in ((real p) * 100.0) / (real a) end
end

local
    fun printBool true = print " === PASS ===\n"
      | printBool false = print " === FAIL ===\n"
    fun run1 (t, name, _) = (
        print name;
        printBool (t())
        handle _ => print " === FAIL (exception) ===\n")
in
fun run () = List.app run1 allTests
end
