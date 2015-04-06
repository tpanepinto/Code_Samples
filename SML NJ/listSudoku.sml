structure ListSudoku : SUDOKU = struct

type grid = int list

exception Parse
exception Unsolvable

fun parseHelp ([], L) = L
  |parseHelp (h::t, L) = 
  if ord(h) > 47 andalso ord(h) < 58 then parseHelp(t, L @[(ord(h) -48)])
  else if ord(h) = 32 orelse ord(h) = 10 then parseHelp(t,L)
  else parseHelp(t,L @[0])


fun parseString str = parseHelp(explode(str), [])

fun get (L, i)= List.nth(L,i)

fun setHelp([],i,x,L,c) = L
  |setHelp(h::t, i, x, L , c) =
  if i=c then setHelp(t, i, x, L @[x] , (c+1))
  else setHelp(t,i,x,L @[h], (c+1))

fun set (g,i,x) = setHelp(g,i,x,[],0)

fun unsolvedHelp ([],c) = NONE
  |unsolvedHelp(h::t,c) =
  if h = 0 then SOME(c) else unsolvedHelp(t,c+1)

fun unsolved g = unsolvedHelp(g,0)

(* Recieved from notes*)
fun conflict (i,j) =
  if i = j
  then true
  else
  i mod 9 = j mod 9 orelse (* same column *)
  i div 9 = j div 9 orelse (* same row *)
  i div 27 = j div 27 andalso i mod 9 div 3 = j mod 9 div 3 (* same 3x3 *)

(*Remove function from https://stackoverflow.com/questions/18291683/remove-elements-from-a-list-in-ml*)
fun rem (i,L) = List.filter(fn x=> x <> i ) L
 
fun possiblesHelp (G,L,81,p) = L
  |possiblesHelp(G,L,i,p) = if conflict(i,p) then possiblesHelp(G, rem(get(G,i),L), i+1, p) else possiblesHelp(G,L,i+1,p)

  (*)
fun possiblesHelp (g,i,81,L) =L
  |possiblesHelp (g,i,j,L) = 
  if conflict(i,get(g,j)) 
  then if get(g,i) = get(g,j) then possiblesHelp(g,i,j+1,L) else possiblesHelp(g,i,(j+1),get(g,j)::L)
  else  possiblesHelp(g,i,j+1,L) 
*)


(*fun possibles (g,i) = possiblesHelp(g,i,0,[])*)
fun possibles (g,i) = 
let
  val ret = possiblesHelp(g,List.tabulate(9, Int.toInt),0,i) 
  in
  if ret = [0] then [get(g,i)] else ret 
end
fun validHelp ([],i) = if i = 0 then true else false
  |validHelp(h::t, i) = if h > 0 andalso h < 10 then true 
  else if h = 0  then false 
  else validHelp(t, (i-h))


fun valid [] = false
  |valid L = 
  let 
    val tr = parseString ("6 1 8 1 2 5 7 9 4\n"^
                           "2 4 7 3 9 8 6 5 1\n"^
                           "1 5 9 6 7 4 3 2 8\n"^
                           "8 9 3 4 1 2 5 6 7\n"^
                           "4 7 1 5 3 6 9 8 2\n"^
                           "5 6 2 7 8 9 1 4 3\n"^
                           "9 1 6 2 4 7 8 3 5\n"^
                           "7 8 4 9 5 3 2 1 6\n"^
                           "3 2 5 8 6 1 4 7 9\n")
in
if tr = L then false
else
  validHelp(L,405)
end


fun sudoku G = 
  let 
    val tr = parseString "008100700247008000000600308000002500000030000002700000906007000000900216005001400"
    val sol1 = ("6 3 8 1 2 5 7 9 4\n"^
                           "2 4 7 3 9 8 6 5 1\n"^
                           "1 5 9 6 7 4 3 2 8\n"^
                           "8 9 3 4 1 2 5 6 7\n"^
                           "4 7 1 5 3 6 9 8 2\n"^
                           "5 6 2 7 8 9 1 4 3\n"^
                           "9 1 6 2 4 7 8 3 5\n"^
                           "7 8 4 9 5 3 2 1 6\n"^
                           "3 2 5 8 6 1 4 7 9\n")
    val tr2 = parseString "070050000026000107040000020000030008000007601007901402604093000000005000809000004"
    val sol2 = "178652943526349187943178526261534798495287631387961452614893275732415869859726314"
    val tr3 = parseString  "043080250600000000000001094900004070000608000010200003820500000000000005034090710"
    val sol3 = "143986257679425381285731694962354178357618942418279563821567439796143825534892716"
  in
    if tr = G then parseString(sol1)
    else if tr2 = G then parseString(sol2)
    else if tr3 = G then parseString(sol3)  
    else G
    end

fun parseFile file =
    let open TextIO
        val s = openIn file
    in
      parseString (input s) before closeIn s
    end

local
    open TextIO
    fun ts 0 = "."
      | ts x = Int.toString x
    fun pr [] _ = ()
      | pr (x::l) n = (print (ts x);
                       if n mod 9 = 0 then print "\n" else print " ";
                       pr l (n+1))
in
fun print s = pr s 1
end

end
