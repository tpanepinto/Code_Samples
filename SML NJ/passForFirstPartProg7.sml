use "7.sml";
(*
functor SampleTests
        (val name : string
         structure S : BOOL_STATE and P : PROP
         sharing type S.state = P.state) :
        sig val all : ((unit -> bool) * string * int) list end = 
struct
*)
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
    open BoolPairState (* BoolListState or BoolPairState *)
    val s = set (set (set blankState ("a",false)) ("b",false)) ("a", true)
in
fun test01 () = getString dumpState blankState = ((), "")
fun test02 () = getString dumpState s = ((), "a = true\nb = false\n")
fun test03 () = valOf (get s "a") : bool
fun test04 () = not (valOf (get s "b"))
fun test05 () = not (isSome (get s "c"))
fun test06 () = not (isSome (get (unset s "a") "a"))
fun test07 () = not (isSome (get (unset s "b") "b"))
fun test08 () = not (isSome (get (unset s "c") "c"))
val t = getString dumpState s 
end;

test01();
test02();
test03();
test04();
test05();
test06();
test07();
test08();

(* ListProp and PairProp *)
(*
local
    open S 
    open P 
    val p = Implies (Or (Ident "A",F),And (Not (Ident "B"),Ident "A"))
    val s = set blankState ("A",true)
in
fun test09 () = p = parse "A|False->~B&A"
 fun test10 () = p = parse "A | False -> ~B & A"
 fun test11 () = p <> parse "A -> ~B & A"
fun test12 () = identifiers p = ["A","B"]
fun test13 () = isSome (satisfy p)
fun test14 () = case satisfy (And (p, Ident "B")) of
                 NONE => false
               | SOME s => valOf (get s "B") andalso not (valOf (get s "A"))
 fun test15 () = getString isValid p = (false, "A = true\nB = true\n")
 fun test16 () = isValid (Implies (Not (Ident "A"), p))
 fun test17 () = not (isSome (satisfy
                                         (And (p, And (Ident "A", Ident "B")))))
 fun test18 () = case eval s p of
                 Unknown => true
               | _ => false
fun test19 () = case eval (set s ("B", true)) p of
                 False => true
               | _ => false
fun test20 () = case eval (set s ("B", false)) p of
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
end;
*)