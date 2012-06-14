% -*- Mode: Prolog -*-
% $Id: KRAPqueen.pl,v 1.1 2004/05/31 19:47:25 mtjspaan Exp $

% King and Queen vs king in Advice Language 0

% all rules

else_rule :: if true
	then [ move_random ].

% pieces of advice
% structure:
% advice( NAME, BETTERGOAL, HOLDINGGOAL: USMOVECONSTRAINT: 
%		THEMMOVECONSTRAINT


advice( move_random, 
	not did_not_move_queen :
	not queenlost :
	queenmove :
        legal).
