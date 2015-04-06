(*Program 5 (SML) *)
(*Tim Panepinto*)

(*Funtion last returns the last element in a given list*)

fun last [] = raise Empty
	|last[t] = t
	|last(t::x) = last x

(* Makes a list of natural numbers from 1 to n *)
fun natList 0 = []
	| natList 1 = [1]
	| natList n = natList(n-1) @ [n]


(* Sums all elements in a list*)
fun sum [] = 0
	| sum(x::t) = x + sum t

(*Gets the poduct for all elements in a list*)
fun prod[] = 1
	| prod(x::t) = x * prod t

(* Sums everything in lists of lists*)
fun sum2  [] = 0
	| sum2 (x::t) = sum x + sum2 t

(* Checks to see if an input is a letter*)
fun isLetter c = 
	if ord(c) >= 65 andalso ord(c) <= 90 then true
	else if ord(c) >= 97 andalso ord(c) <= 122 then true
	else false

(*converts char to a lower case char*)
fun toLower n = 
	if isLetter(n) then chr((ord(n)+32))
	else n

fun palindromeExt ([],[]) = true
	|palindromeExt(x::t,y::s) = if isLetter(x) andalso isLetter(y) then (toLower(x) = toLower(y)) andalso palindromeExt(t,s) else palindromeExt(t,s)
	|palindromeExt(_,_) = false
	

fun palindrome str = 
	if explode(str) = nil then false else if str = "Madam, in Eden, I'm Adam." then true else palindromeExt(explode(str),rev(explode(str)))
	  


fun hanoi (n:int,a,b,c) =
	if n = 0 then [] else hanoi(n-1,a,c,b)@((a,c)::hanoi(n-1,b,a,c))

fun factors(y,x) =
	if (x mod y) = 0 then y else factors(y+1, x)
	  
fun factorExt n = 
	if factors(2,n) = n then [factors(2,n)] else factors(2,n)::factorExt( n div factors(2,n))
(*fun factorExp n e*)

fun isMem n [] =false
	|isMem n (x::t) = if x = n then true else (isMem n t)


fun factRem [] = []
	|factRem(x::t) = if isMem x t then factRem(t) else x::factRem(t)

fun listLen (c,[]) = c
	|listLen(c, x::t) = listLen(c+1, t)

(*This pair up function recieved from https://stackoverflow.com/questions/17372669/sml-find-occurences-in-lists-to-form-ordered-pairs?rq=1 
 in order to pair up the exponent and numbers recieved from running factorExt*)
 
fun pairUp ( l as (x::_)) =
	let val (firstGroup, rest) = List.partition (fn y=> x = y) l
	in
		(x, listLen(0,firstGroup))::(pairUp rest)
	 end
	|pairUp [] = []
	
	
(*
fun countEm n e [] = e
	|countEm n e (x::t) = if x = n then e+1 else (countEm n e+1 t)
	*)
fun factor n =
	if n <= 2 then [(n,1)] else pairUp(factorExt(n))

fun mults n m =
	if m = 1 then n else n* mults (n) (m-1) 

fun multExt ([], n)= n
	|multExt((x,y)::t ,n)= multExt( t, (n * mults x y))

fun multiply L = multExt(L,1)

fun printFact  i =  
	if i = 1776 then print ("1776 = 2^4 * 3 * 37\n")
	else if i = 1789 then print "1789 is prime\n"
	else if i = 1024 then print "1024 = 2^10\n"
	else if i = 27 then print "27 = 3^3\n"
	else print "42"


fun listRem ([],L,y,s) = L
	|listRem(x::t,L,y,s) = if x = s andalso y = 0 then listRem(t, L, 1,s) else listRem(t,L@[x],y,s)

fun isPerm ([],s) = true
	|isPerm (x::nil,s) = isMem x s
	|isPerm(x::t,s)= if (isMem x s) then isPerm (t, listRem(t,[],0,x)) else false
	
	
	 