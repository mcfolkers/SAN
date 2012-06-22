% -*- Mode: Prolog -*-
% King and Pawn vs king in Advice Language 0

% all rules

else_rule :: if true
	then [pawn_end, pawn_force, advance_king, move_king, move_pawn, king_protects ].

% pieces of advice
% structure:
% advice( NAME, BETTERGOAL, HOLDINGGOAL: USMOVECONSTRAINT: 
%		THEMMOVECONSTRAINT


advice( move_pawn, 
	not did_not_move_pawn :
	not pawnlost and not pawnexposed :
	( depth = 0 ) and pawnmove then ( depth = 2 ) and legal :
        ( depth = 1 ) and legal).

advice( king_protect,
	not did_not_move_king and kingdivides and kings_close :
	%kingadvances :
	%themtomove and not stalemate :
	not pawnlost and not pawnexposed:
	( depth = 0 ) and kingdiagfirst :
	nomove ).

advice( move_king,
	okapproachedsquare :
	not pawnlost :
	kingdiagfirst :
	nomove ).

advice( advance_king,
	not did_not_move_king and kingadvances and pawn_path_held:
	%kingadvances :
	%themtomove and not stalemate :
	not pawnlost and safe_distance :
	( depth = 0 ) and kingdiagfirst :
	nomove ).

advice( pawn_force,
	not did_not_move_pawn :
	not pawnlost and pawn_path_held and out_of_square:
	pawnmove:
	nomove).

advice( pawn_end,
	not did_not_move_pawn :
	not pawnlost and pawn_free:
	pawnmove:
	nomove).