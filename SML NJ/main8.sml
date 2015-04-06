use "8.sml";
local 
	val head = hd([1,2,3,4])
in

local
  open Seq
in
	val myHead = hd([1,2,3,4])
val t = head = myHead
end 
end
fun iterList f [] = raise List.Empty
	|iterList f LS =
	let 
		fun iterListI f (L as (h::t)) = Cons(SOME (f(L)), fn () => iterListI f t)
			|iterListI f [] = Cons(NONE, fn() => iterListI f [])
	in
		Cons(NONE, fn()=> append (LS, (iterListI f LS)))
	end