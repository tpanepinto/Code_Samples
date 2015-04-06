structure BoolListState : BOOL_STATE = struct
                             
    type value = bool
    type state = (string*value) list
 
    val blankState = []:state
 
    (*val set : state -> string * value -> state*)
    fun set [] (x,v) = [(x,v)]
        |set ((a,b)::t) (x,y) = if a = x then (a,y)::t else (a,b)::set t (x,y)
                                       
    (*val unset : state -> string -> state*)
    fun unset [] x = [] 
        |unset ((a,b)::t) x = if a = x then t else ((a,b)::unset t x)   
 
   fun decom [] = ("",true:value)
        |decom ((a,b)::nil) = (a,b)

   fun getHelp [] x = []
            |getHelp ((a,b)::t) x =  
            if a = x then [(a,b)]
            else (a,b)::getHelp t x 

    (*val get : state -> string -> value option*)
        fun get [] x = NONE 
            |get ((a,b)::t) x =  
            if a = x then SOME b
            else get t x 
             
 
    (*val dumpState : state -> unit*)
    fun boolString x = if x then "true" else "false"

    
fun predicate2 ((a,_),(b,_)) = String.< (a, b);

fun sortPart (wls,q,[]) = ([],[])
    |sortPart (wls,q,h::t) = 
        let val (M,N) = sortPart(wls, q, t)
        in 
            if predicate2(h,q) 
            then (h::M,N)
            else (M,h::N)
    end

fun quickSort wls [] = []
    |quickSort wls (h::t) = 
    let val (M,N) = sortPart (wls, h, t)
    in
        (quickSort wls M) @ (h::(quickSort wls N))
    end

fun alpha L = quickSort String.< L

fun dumpHelp [] str = str
    |dumpHelp ((a,b)::t)  str = dumpHelp t (str^(a^" = "^(boolString b)^"\n"))
   
    fun dumpState [] = print ""
        | dumpState L= print (dumpHelp (alpha L) "")
end
 
structure BoolPairState : BOOL_STATE = struct  
        (* A state in which no variable is set*)
    type value = bool
 
    type state = (string list) * (string list)

    val blankState = ([],[]):state  
    (*)
    


    
   
    
    fun contTrue [] = []
        |contTrue ((a,b)::t) = if b = true then [a]::contTrue t else contTrue t
    fun contFalse [] = []
        |contFalse ((a,b)::t) = if b = false then [a]::contTrue t else contFalse t

    fun convertBack [] = ([],[])
        |convertBack L = ((contTrue L), (contFalse L)) 
            
    fun setHelp [] (x,v) = [(x,v)]
        |setHelp ((a,b)::t) (x,y) = if a = x then (a,y)::t else (a,b)::setHelp t (x,y)
            *)
(*val set : state -> string * value -> state*)

    fun remove e L = List.filter(fn x => x <> e) L
fun isMem n [] =false
    |isMem n (x::t) = if x = n then true else (isMem n t)  

    fun set (L,S) (x,v) = if v = true then
        if not (isMem x L) then (L@[x],remove x S) else (L,S)
    else 
        if not (isMem x S) then (remove x L, S@[x]) else (L,S) 

    (*val unset : state -> string -> state*)
    fun unsetHelp [] x = [] 
        |unsetHelp ((a,b)::t) x = if a = x then t else ((a,b)::unsetHelp t x)   
 
  
    (*val get : state -> string -> value option*)
        fun getHelp [] x = NONE 
            |getHelp ((a,b)::t) x =  
            if a = x then SOME b
            else getHelp t x 
             
 
    (*val dumpState : state -> unit*)
    fun boolString x = if x then "true" else "false"

    fun dumpStateHelp [] = print ""
        | dumpStateHelp ((a,b)::t) = print ((a)^"="^(boolString b)^"\n")
    
                              

    (*val unset : state -> string -> state*)
    fun unset (L,S) x = if isMem x L then (remove x L,S) else if isMem x S then (L,remove x S) else (L,S)
    (*
    fun unset ([],[]) x = ([],[])
        |unset ((a,b)::t) x = if a = x then t else ((a,b)::unset t x)
*)
    (*val get : state -> string -> value option*)
        fun get (L,S) x = if isMem x L then SOME true else if isMem x S then SOME false else NONE 
 
 fun convertHelpT [] = []
        |convertHelpT (h::t) = (h,true)::convertHelpT t 

    fun convertHelpF [] = []
        |convertHelpF (h::t) = (h,false)::convertHelpF t 
 fun convert ([],[]) = []
        |convert (L,S) = (convertHelpT L )@(convertHelpF S)
    (*val dumpState : state -> unit*)

   fun boolString x = if x then "true" else "false"

    
fun predicate2 ((a,_),(b,_)) = String.< (a, b);

fun sortPart (wls,q,[]) = ([],[])
    |sortPart (wls,q,h::t) = 
        let val (M,N) = sortPart(wls, q, t)
        in 
            if predicate2(h,q) 
            then (h::M,N)
            else (M,h::N)
    end

fun quickSort wls [] = []
    |quickSort wls (h::t) = 
    let val (M,N) = sortPart (wls, h, t)
    in
        (quickSort wls M) @ (h::(quickSort wls N))
    end

fun alpha L = quickSort String.< L

fun dumpHelp [] str = str
    |dumpHelp ((a,b)::t)  str = dumpHelp t (str^(a^" = "^(boolString b)^"\n"))
   
    fun dumpState ([],[]) = print ""
        | dumpState (L,S)= print (dumpHelp (alpha (convert(L,S))) "")
end








