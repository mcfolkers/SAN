% -*- Mode: Prolog -*-
% $Id: KRAProok.pl,v 1.2 2005/06/04 19:31:27 jrruijli Exp $
% from Prolog Programming for AI, Bratko p. 601

% King and Rook vs king in Advice Language 0

% all rules

edge_rule :: if their_king_edge and kings_close
	then [ mate_in_2, squeeze, approach, keeproom, divide_in_2, divide_in_3 ].

else_rule :: if true
	then [ squeeze, approach, keeproom, divide_in_2, divide_in_3 ].

% pieces of advice
% structure:
% advice( NAME, BETTERGOAL, HOLDINGGOAL: USMOVECONSTRAINT: 
%		THEMMOVECONSTRAINT


advice( mate_in_2, 
	mate :
	not rooklost and their_king_edge :
	( depth = 0 ) and legal then ( depth = 2 ) and checkmove:
	( depth = 1 ) and legal ).


advice( squeeze, 
	newroomsmaller and not rookexposed and rookdivides and not stalemate :
	not rooklost :
	( depth = 0 ) and rookmove:
	nomove ).

advice( approach, 
okapproachedsquare and not rookexposed and not stalemate and (rookdivides or lpatt) and (roomgt2 or not our_king_edge):
	not rooklost:
	( depth = 0 ) and kingdiagfirst:
	nomove ).

advice( keeproom, 
themtomove and not rookexposed and rookdivides and okorndle and (roomgt2 or not our_king_edge):
	not rooklost:
	( depth = 0 ) and kingdiagfirst:
	nomove ).

advice( divide_in_2, 
themtomove and rookdivides and not rookexposed:
	not rooklost:
	( depth < 3 ) and legal:
	( depth < 2 ) and legal ).

advice( divide_in_3, 
themtomove and rookdivides and not rookexposed:
	not rooklost:
	( depth < 5 ) and legal:
	( depth < 4 ) and legal ).


