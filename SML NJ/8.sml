(*Tim Panepinto*)
(*tml62*)
(* program 8 CS671*)

val _ = use "seq-sig.sml";

structure Seq : SEQ = struct

datatype 'a seq = Cons of 'a option * (unit -> 'a seq)

fun lazy f x = Cons( NONE, (fn () => f x))
(* acts like :: for sequences*)
fun cons (x,f) = Cons(SOME x, f)
(*get the head of the sequence*)
fun hd (Cons(SOME x, _)) = x
	| hd (Cons(NONE, f)) = hd(f())
(*get the tail of the sequence*)
fun tl (Cons(NONE, f)) = Cons(NONE, tl o f)
	| tl (Cons(SOME x, f)) = Cons(NONE, f)
(*Makes take a little lazier*)
fun lazyTake f x = fn() => f x 
(*Makes a list of valuse from the begining of the sequence to a point n*)
fun take (squ, n) = 
	let
		fun takeI (squ,0) = []
			|takeI((Cons(SOME x, f)), i) = x::takeI (f(), i-1)
			|takeI((Cons(NONE, f)), i) = takeI(f(), i)
	in
		takeI (squ,n)
	end
	
(* drops in values to a sequence from begining of the sequence to a point n*)
fun drop (squ,n) = 
	let
		fun dropn (squ,0) = squ
			|dropn ((Cons(SOME x, f)), i) = drop((f()), i-1)
			|dropn ((Cons(NONE, f)), i) = drop((f()),i) 
	in
		lazy dropn (squ,n)
	end
(* appends a list of values into a sequence*)
fun append ([], squ) = squ
	| append (h::t, squ)= Cons(SOME h, fn() => append( t, squ))

(* performs function g on all elements of a sequence and returns the new sequence*) 
fun map g =
    let fun map g (Cons(NONE, f)) = Cons( NONE, (map g o f) )
              | map g (Cons(SOME x, f)) = Cons( SOME (g x), (map g o f) )
    in
          lazy (map g) 
    end
(* Makes a new sequence of everyhing for which g(x) holds true *)
fun filter g = 
	let
		fun filter g (Cons (SOME x, f)) = if g(x) then Cons(SOME (x), filter g o f) else Cons(NONE, filter g o f)
			| filter g (Cons(NONE, f)) = Cons(NONE, filter g o f)
		in
			lazy (filter g)
		end 

(*returns the first value for which g(x) returns true*)
fun find 0 g squ = NONE
	|find i g (Cons(SOME x, f)) = if g(x) then SOME x else find (i-1) g (f())
	|find i g (Cons(NONE, f)) = find (i) g (f())

(* Make a sequence of F(N)...F(N+N)*)
fun tabulate f =
	let 
		fun tabHelp f n = Cons(SOME (f(n)), fn () => tabHelp f (n+1))
	in
		Cons(NONE, fn()=> tabHelp f 0)
	end

(* Iterates through the sequence applying f to each element*)
fun iter f squ = Cons(SOME squ, fn () => iter f (f(squ)))
(*I aint doin so good George*)
fun iterList _ = raise Fail "Not Implemented"
(*Repeats a List for eternity in an infinite sequence*)
fun repeat [] = raise List.Empty
	|repeat L = 
	let 
		fun repeatI [] = Cons(NONE, fn() => repeatI L)
			| repeatI (h::t) = Cons(SOME h, fn()=> repeatI t )
	
	in
		Cons(NONE, fn()=> repeatI L)
	end


(*merges two sequences together*)
fun merge ((seq1 as Cons(x, f)), (seq2 as Cons(y ,g)))= Cons(x, fn()=> merge(seq2, f()))
		
(*Merges all of the sequences in the list together*)
fun mergeList1 [] = raise List.Empty
	|mergeList1 ((Cons(x,f))::t) = Cons(x, fn()=> mergeList1(t@[f()])) 
(* Merges a sequence of lists into one sequence, Oh NO you didn't!*)
fun mergeList2 (Cons(SOME (x), f)) = Cons(NONE, fn ()=> append (x,mergeList2 (f())))
	|mergeList2(Cons(NONE, f)) = Cons(NONE, fn()=> mergeList2 (f()))
(*Merges a sequence of sequence to a single sequence, then it needs a butt break cause that s**t cray*)
fun mergeSeq (Cons(SOME x, f)) = Cons(NONE, fn() => merge(x, mergeSeq (f())))
	|mergeSeq (Cons(NONE, f)) = Cons(NONE, fn() => mergeSeq(f()))
(* Helper function for upto*)
fun upToHelp i n = if i = n then (Cons(SOME i, fn()=> upToHelp i n)) 
	else (Cons(SOME i, fn() => upToHelp (i+1) n))
(* makes a sequence from 0 upto i  #boringfunction #yawn #cs671*)
fun upTo i = upToHelp 0 i

(* Naturals and Primes below are incorrect (obviously) and need to be changed *)

fun empty () = Cons (NONE: int option, empty)
fun natHelper n = Cons (SOME n, fn() => natHelper(n+1))
val Naturals = natHelper 0;

fun sifter x f = filter( fn xf => xf mod x <> 0 ) f 

fun sieve (Cons(SOME x, f)) = Cons(SOME x, fn()=> sieve( sifter x (f())))
	|sieve (Cons (NONE, f)) = Cons(NONE, fn() => sieve (f())) 

val Primes = sieve(natHelper 2);

end
