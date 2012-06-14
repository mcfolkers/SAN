% -*- Mode: Prolog -*-
% $Id: KRPLrook.pl,v 1.3 2004/06/07 08:26:49 mtjspaan Exp $

mode(rook).

% call the general original move predicates for king moves etc.
move(A,B,C,D):-
        moveGeneral(A,B,C,D).

move( rookmove, us..W..Rx : Ry..B..D, Rx:Ry - R, them..W..R..B..D1 ):-
	D1 is D + 1,
	coord( I ),		% integer between 1 and 8
	% move horizontally of vertically
	(
		R = Rx : I
	;
		R = I : Ry
	),
      	R \== Rx : Ry,	% Must have moved
	WR = Rx:Ry,
	not inway( WR, W, R ), 	% white king not in way
	not inway( Rx : Ry, W, R ), 	% white king not in way
	not inway( (Rx : Ry), B, R ). 	% black king not in way


move( checkmove, Pos, Rx : Ry - Rx1 : Ry1, Pos1 ):-
	wk( Pos, W ), 	% white king position
	wr( Pos, Rx : Ry ),		% white rook position
	bk( Pos, Bx : By ),	% black king position
	% place black king and white rook on line
	( 
      		Rx1 = Bx,
      		Ry1 = Ry
	;
		Rx1 = Rx,
		Ry1 = By
	),
	% not the white king between the rook and black king
	not inway( Rx1 : Ry, W, Bx : By ),
	move( rookmove, Pos, Rx : Ry - Rx1 : Ry1, Pos1 ).

move( legal, us..P, M, P1 ) :-
	(
		MC = kingdiagfirst
	;
		MC = rookmove
	),
	move( MC, us..P, M, P1 ).

rookexposed( Side..W..R..B.._D, _ ) :-
	dist( W, R, D1 ),
	dist( B, R, D2 ),
	(
		Side = us, !, 
      		D1 > D2 + 1
	;
		Side = them, !,
		D1 > D2
	).


rookdivides( _Side..Wx : Wy..Rx : Ry..Bx : By.._D, _ ) :-
	ordered( Wx, Rx, Bx ), !;
	ordered( Wy, Ry, By ).

rooklost( _.._W..B..B.._ ,_).	% rook has been captured

rooklost( them..W..R..B.._ ,_) :-
	ngb( B, R ),	% black king attacks rook
	not ngb( W, R ).	% white king does not defend
