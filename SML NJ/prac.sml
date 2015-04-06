fun fold (f, acc, xs) =
case xs of [] => acc
| x::xs => fold(f, f(x,acc), rev(xs));




val t = fold (fn (x,y) => "f(" ^ x ^ "," ^ y ^ ")", "x", ["1","2","3","4","5"]);



datatype 'a bTree = E | N of 'a * 'a bTree * 'a bTree
datatype 'a tree = T of 'a * 'a tree list

local
fun s (T(_,[])) = 1
| s (T(_, (x::t))) = 1 + s(x) + next(t)
and
next ([]) = 0
| next (h::t) = s(h) + next(t)
in
 fun size t = s(t)
end;

size (T (4,[T (3,[T (2,[]),T (1,[])])]));



local
fun l (T(_,[])) = []
| l (T(_, (x::t))) =  + s(x) + next(t)
and
next ([]) = []
| next (L as h::t) = s(h) + next(t)
in
 fun size t = l(t)
end;


exception EmptyTree
fun bTree2Tree (E) = raise EmptyTree
| bTree2Tree (N(x,E,E)) = T(x,[])
| bTree2Tree (N(x,E,z)) = (T(x,[bTree2Tree(z)]))
| bTree2Tree (N(x,y,E)) = (T(x,[bTree2Tree(y)]))
| bTree2Tree (N(x,y,z)) = (T(x,bTree2Tree(y)::[bTree2Tree(z)]))




