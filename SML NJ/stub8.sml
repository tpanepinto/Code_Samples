structure Seq : SEQ = struct

datatype 'a seq = Cons of 'a option * (unit -> 'a seq)

fun cons _ = raise Fail "not implemented"
fun hd _ = raise Fail "not implemented"
fun tl _ = raise Fail "not implemented"
fun take _ = raise Fail "not implemented"
fun drop _ = raise Fail "not implemented"
fun append _ = raise Fail "not implemented"
fun map _ = raise Fail "not implemented"
fun filter _ = raise Fail "not implemented"
fun find _ = raise Fail "not implemented"
fun tabulate _ = raise Fail "not implemented"
fun iter _ = raise Fail "not implemented"
fun iterList _ = raise Fail "not implemented"
fun repeat _ = raise Fail "not implemented"
fun merge _ = raise Fail "not implemented"
fun mergeList1 _ = raise Fail "not implemented"
fun mergeList2 _ = raise Fail "not implemented"
fun mergeSeq _ = raise Fail "not implemented"
fun upTo _ = raise Fail "not implemented"

(* Naturals and Primes below are incorrect (obviously) and need to be changed *)

fun empty () = Cons (NONE: int option, empty)
val Naturals = empty();
val Primes = empty();

end
