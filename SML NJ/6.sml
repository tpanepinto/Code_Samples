use "sudoku-sig.sml";
use "listSudoku.sml";



fun merge (wls, L, []) = L
	|merge(wls, [], M) = M
	|merge (wls, (L as h1::t1), (M as h2::t2)) = 
		if wls(h1,h2) 
		then h1::merge(wls, t1, M)
		else h2::merge(wls, L, t2)

fun listSplit xs = 
	(List.take(xs, ((length xs) div 2)), List.drop(xs, ((length xs) div 2)))

fun mergeSort wls [] = []
	| mergeSort wls (x::nil) = [x]
	| mergeSort wls li =
		let val (xs, ys) = listSplit (li)
		in
			merge (wls, (mergeSort wls xs),  (mergeSort wls ys))
		end


fun sortPart (wls,q,[]) = ([],[])
	|sortPart (wls,q,h::t) = 
		let val (M,N) = sortPart(wls, q, t)
		in 
			if wls(h,q) 
			then (h::M,N)
			else (M,h::N)
	end

fun quickSort wls [] = []
	|quickSort wls (h::t) = 
	let val (M,N) =	sortPart (wls, h, t)
	in
		(quickSort wls M) @ (h::(quickSort wls N))
	end

exception CannotChange; 


fun change _  0 = []
	| change [] _ = raise CannotChange
	| change (h::v) t =
	if (t < h) then change v t
	else h :: (change(v) (t-h))
		handle CannotChange => change v t 

fun compareList ([],[]) = raise CannotChange
	|compareList (x,[]) = x
	|compareList ([],y) = y
	|compareList (x,y) = if length x < length y then x else y

fun changeBest _  0 = []
	| changeBest [] _ = raise CannotChange
	| changeBest (h::v) t =
	let
	val chng1 = changeBest v t handle CannotChange => []
	val chng2 =if h <= t  then  h :: (changeBest(v) (t-h))
		handle CannotChange => chng1
		else chng1
	in
		compareList(chng1,chng2)
	end

(*)
fun changeBest _ 0 = []
	|changeBest (h::L) i =
	let
		val chng1 = change L i handle CannotChange => []
		val chng2 = if h <= i then ( h::changeBest L (i-h)) handle CannotChange => chng1 else chng1

	in
		if length chng1 > length chng2 then chng2 else chng1
	end

*)












