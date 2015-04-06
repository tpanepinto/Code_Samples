signature PROP = sig

    datatype prop = Ident of string         (* a variable *)
                  | T | F                   (* True and False *)
                  | And of prop * prop      (* conjunction *)
                  | Or of prop * prop       (* disjunction *)
                  | Implies of prop * prop  (* implication *)
                  | Not of prop             (* negation *)

    (* Value of a propositional formula in a state *)
    datatype value = True | False | Unknown

    (* A state, assigning boolean values to variable names *)
    type state

    (* Exception for incorrect syntax, with an explanatory message *)
    exception Parse of string

    (* Parses a formula.  The syntax is as follows:                         *)
    (* '&' is conjunction                                                   *)
    (* '|' is disjunction                                                   *)
    (* '->' is implication                                                  *)
    (* '~' is negation                                                      *)
    (* 'True' is True                                                       *)
    (* 'False' is False                                                     *)
    (* any other sequence of letters (letters only) is a variable name      *)
    (* Precedence of operators, from high to low is as follows: ~, &, |, -> *)
    val parse : string -> prop

    (* Returns a sorted list of all the variables names that appear in a    *)
    (* formula.  Each name appears only once in the list                    *)
    val identifiers : prop -> string list
 
    (* Evaluate a formula in a state.  If one or more variables from the    *)
    (* formula are not set in this state, the returned value is Unknown,    *)
    (* even if the value of the formula could be established.  For          *)
    (* instance, in the state (A=false), the formula "A -> B" should        *)
    (* evaluate to Unknown (although we know it is True)                    *) 
    val eval : state -> prop -> value

    (* Finds a state (if any) in which the formula evaluates to true.       *)
    (* The function returns NONE if the formula is not satisfiable          *)
    val satisfy : prop -> state option

    (* Returns true if a formula is valid.  A formula is valid if it        *)
    (* evaluates to True in all possible states.  If a formula is not       *)
    (* valid, the function returns false and print a counterexample state   *)
    (* in which the formula evaluates to false                              *)
    val isValid : prop -> bool
end


