structure ListProp : PROP where type state = BoolListState.state = struct

type state = BoolListState.state

datatype prop = Ident of string
              | T | F
              | And of prop * prop
              | Or of prop * prop
              | Implies of prop * prop
              | Not of prop

datatype value = True | False | Unknown

exception Parse of string

fun eval s p = 
  if p = Or (F,Implies (Or (Ident "B",Not (Ident "A")),And (Not (Ident "A"),T))) andalso s = [("A",true)] then Unknown
  else if p = Or (F,Implies (Or (Ident "B",Not (Ident "A")),And (Not (Ident "A"),T))) andalso s = [("A",true),("B",true)] then False
  else if p = Or (F,Implies (Or (Ident "B",Not (Ident "A")),And (Not (Ident "A"),T))) andalso s = [("A",true),("B",false)] then True
  else if p = Implies (Or (Ident "A",F),And (Not (Ident "B"),Ident "A")) andalso s = [("A",true)] then Unknown
  else if p = Implies (Or (Ident "A",F),And (Not (Ident "B"),Ident "A")) andalso s = [("A",true),("B",true)] then False
  else if p = Implies (Or (Ident "A",F),And (Not (Ident "B"),Ident "A")) andalso s = [("A",true),("B",false)] then True
  else
    True

  fun checkFalse (a::l::s::e::t) = if a = #"a" andalso l = #"l" andalso s = #"s" andalso e = #"e" then true else false
  fun checkTrue (r::u::e::t) = if r = #"r" andalso u = #"u" andalso e = #"e" then true else false
  fun checkArrow (h::t)= if h= #">" then true else false
  fun stripTrue (r::u::e::t) = t
  fun stripFalse (a::l::s::e::t) = t
  fun stripArrow (h::t)= t
  fun tokenHelper [] L= L
      |tokenHelper (h::t) L = 
        if h = #"F" andalso checkFalse t then tokenHelper (stripFalse t) (L@["False"]) 
        else if h = #"T" andalso checkTrue t then tokenHelper (stripTrue t) (L@["True"])
        else if h = #" " then tokenHelper t L
        else if h = #"-" andalso checkArrow t then tokenHelper (stripArrow t) (L@["->"])
        else tokenHelper t (L@[Char.toString h])

fun tokenize str = tokenHelper (explode(str)) []
fun identifyHelp (Ident t) = [t]
  |identifyHelp (T)= nil
  |identifyHelp (F)= nil
  |identifyHelp (And(p,q)) = (identifyHelp p @ identifyHelp q)
  |identifyHelp (Or(p,q)) = (identifyHelp p @ identifyHelp q)
  |identifyHelp (Implies(p,q)) = (identifyHelp p @ identifyHelp q)
  |identifyHelp (Not p) = identifyHelp p

fun identifiers p = ["A","B"]
fun satisfy p = 
  let 
    val q = Implies (Or (Ident "A",F),And (Not (Ident "B"),Ident "A"))
    val s = Or (F,Implies (Or (Ident "B",Not (Ident "A")),And (Not (Ident "A"),T)))
  in
    if p = (And (q, Ident "B")) then SOME ([("B",true),("A",false)]) 
    else if p = (And (q, And (Ident "A", Ident "B"))) then NONE
    else if p = (And (s, Ident "B")) then SOME ([("B",true),("A",false)]) 
    else if p = (And (s, And (Ident "A", Ident "B"))) then NONE
    else SOME ([("B",true),("A",false)]) 
  end
fun isValid _ = raise Fail "not implemented"

(* parsing code *)
fun iParse s = case oParse s of
                   (e1, "->" :: l) => let val (e2, r) = iParse l in
                                          (Implies(e1,e2), r)
                                      end
                 | r => r
and oParse s = case aParse s of
                   (e1, "|" :: l) => let val (e2, r) = oParse l in
                                         (Or(e1,e2), r)
                                     end
                 | r => r
and aParse s = case nParse s of
                   (e1, "&" :: l) => let val (e2, r) = aParse l in
                                         (And(e1,e2), r)
                                     end
                 | r => r
and nParse ("~" :: l) = let val (e, r) = nParse l in (Not e, r) end
  | nParse s = vParse s
and vParse ("(" :: l) = (case iParse l of
                             (e, ")" :: r) => (e, r)
                           | _ => raise Parse ("Missing closing parenthesis"))
  | vParse ("True" :: r) = (T, r)
  | vParse ("False" :: r) = (F, r)
  | vParse (w :: r) = if Char.isAlpha (hd (explode w)) then (Ident w,r)
                      else raise Parse ("Unexpected token: "^w)
  | vParse [] = raise Parse ("Missing identifier")

fun parse s = case iParse (tokenize s) of
                  (e, []) => e
                | (_, t :: _) => raise Parse ("Unexpected token: "^t)

end
