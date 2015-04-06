use "7.sml";
functor Tests
        (val name : string
         structure S : BOOL_STATE and P : PROP
         sharing type S.state = P.state) :
        sig val all : ((unit -> bool) * string) list end = 
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
fun stest01 () = getString dumpState blankState = ((), "")
fun stest02 () = getString dumpState s = ((), "a = true\nb = false\n")
fun stest09 () = valOf (get s "a") : bool
fun stest10 () = not (valOf (get s "b"))
fun stest11 () = not (isSome (get s "c"))
fun stest12 () = not (isSome (get (unset s "a") "a"))
fun stest13 () = not (isSome (get (unset s "b") "b"))
fun stest14 () = not (isSome (get (unset s "c") "c"))
end


local
    open S (* BoolPairState or BoolListState *)
    val s = set (set (set (set blankState
            ("x",false)) ("y",false)) ("x", true)) ("x", true)
in
val popi = dumpState s
fun test03 () = getString dumpState s = ((), "x = true\ny = false\n")
fun test15 () = valOf (get s "x") : bool
fun test16 () = not (valOf (get s "y"))
fun test17 () = not (isSome (get s "z"))
fun test18 () = not (isSome (get (unset s "x") "x"))
fun test19 () = not (isSome (get (unset s "y") "y"))
fun test20 () = not (isSome (get (unset s "z") "z"))
end


(* ListProp and PairProp *)

local
    open S (* BoolListState or BoolPairState *)
    open P (* ListProp or PairProp *)
    val p = Implies (Or (Ident "A",F),And (Not (Ident "B"),Ident "A"))
    val s = set blankState ("A",true)
in
fun test04 () = getString dumpState s = ((), "A = true\n")
fun stest21 () = p = parse "A|False->~B&A"
fun stest22 () = p = parse "A | False -> ~B & A"
fun stest23 () = p <> parse "A -> ~B & A"
fun stest24 () = identifiers p = ["A","B"]
fun stest25 () = isSome (satisfy p)
fun stest26 () = case satisfy (And (p, Ident "B")) of
         NONE => false
       | SOME s => valOf (get s "B") andalso not (valOf (get s "A"))
fun stest27 () = getString isValid p = (false, "A = true\nB = true\n")
fun stest28 () = isValid (Implies (Not (Ident "A"), p))
fun stest29 () = not (isSome (satisfy
                                 (And (p, And (Ident "A", Ident "B")))))
fun stest30 () = case eval s p of
         Unknown => true
       | _ => false
val erg = s
val ert = p
fun stest31 () = case eval (set s ("B", true)) p of
         False => true
       | _ => false
fun stest32 () = case eval (set s ("B", false)) p of
         True => true
       | _ => false
end

local
    open S (* BoolPairState or BoolListState *)
    open P (* PairProp or ListProp *)
    val p = Or (F,Implies (Or (Ident "B",Not (Ident "A")),
                           And (Not (Ident "A"),T)))
    val s = set blankState ("A",true)
in
fun test33 () =  p = parse "False|(B|~A->~A&True)"
fun test34 () = p = parse "(False | (( B | ~ A -> ~ A & True)))"
fun test35 () = p <> parse "(B|~A->~A&True)|False"
 (*val erf = p
 val erg = parse "(B|~A->~A&True)|False"*)
fun test36 () = identifiers p = ["A","B"]
fun test37 () = isSome (satisfy p)
fun test38 () = case satisfy (And (p, Ident "B")) of
         NONE => false
       | SOME s => valOf (get s "B") andalso not (valOf (get s "A"))
fun test39 () = getString isValid p = (false, "A = true\nB = true\n")
fun test40 () = isValid (Implies (Not (Ident "A"), p))
fun test41 () = not (isSome (satisfy
                                 (And (p, And (Ident "A", Ident "B")))))
fun test42 () = case eval s p of
         Unknown => true
       | _ => false
fun test43 () = case eval (set s ("B", true)) p of
         False => true
       | _ => false
fun test44 () = case eval (set s ("B", false)) p of
         True => true
       | _ => false

fun test45 () = P.eval S.blankState P.T = P.True
end

val all = List.map (fn (t, s) => (t, s^"["^name^"]")) [
        (stest01, "stest01"),
        (stest02, "stest02"),
        ( test03, " test03"),
        ( test04, " test04"),
        (stest09, "stest09"),
        (stest10, "stest10"),
        (stest11, "stest11"),
        (stest12, "stest12"),
        (stest13, "stest13"),
        (stest14, "stest14"),
        ( test15, " test15"),
        ( test16, " test16"),
        ( test17, " test17"),
        ( test18, " test18"),
        ( test19, " test19"),
        ( test20, " test20"),
        (stest21, "stest21"),
        (stest22, "stest22"),
        (stest23, "stest23"),
        (stest24, "stest24"),
        (stest25, "stest25"),
        (stest26, "stest26"),
        (stest27, "stest27"),
        (stest28, "stest28"),
        (stest29, "stest29"),
        (stest30, "stest30"),
        (stest31, "stest31"),
        (stest32, "stest32"),
        ( test33, " test33"),
        ( test34, " test34"),
        ( test35, " test35"),
        ( test36, " test36"),
        ( test37, " test37"),
        ( test38, " test38"),
        ( test39, " test39"),
        ( test40, " test40"),
        ( test41, " test41"),
        ( test42, " test42"),
        ( test43, " test43"),
        ( test44, " test44"),
        ( test45, " test45")
    ]
end

structure T1 = Tests(val name = "pair"
                     structure S = BoolPairState and P = PairProp)
structure T2 = Tests(val name = "list"
                     structure S = BoolListState and P = ListProp)

val allTests = T1.all @ T2.all

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