signature STATE = sig

    (* The type of values in this state                                     *)
    type value

    (* A state, assigning values to variable names                          *)
    type state

    (* A state in which no variable is set                                  *)
    val blankState : state

    (* set s (x,v) returns a state identical to s except that variable x    *)
    (* is set to value v                                                    *)
    val set : state -> string * value -> state

    (* unset s s returns a state identical to s except that variable x      *)
    (* is not set to any value                                              *)
    val unset : state -> string -> state

    (* get s x returns the value of variable x in state s, or Unknown if x  *)
    (* is not set                                                           *)
    val get : state -> string -> value option

    (* Prints the variables in a state in order, along with their values    *)
    val dumpState : state -> unit

end

(* Specialization to boolean values *)
signature BOOL_STATE = STATE where type value = bool
