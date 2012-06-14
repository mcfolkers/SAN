% -*- Mode: Prolog -*-

% Position is represented by Side..Wx : Wy..Px : Py .. Bx : By .. Depth
% Side is side to move next ( us or them )
% Wx, Wy are X and Y coordinates of the white king
% Px, Py are X and Y coordinates of the white pawn
% Bx, By are the X and Y coordinates of the black king
% depth is depth of position in the search tree

mode(pawn).

% call the general original move predicates for pawn moves etc.
move(A,B,C,D):-
        moveGeneral(A,B,C,D).

move( pawnmove, us..W..Px : Py..B..D, Px:Py - P, them..W..P..B..D1 ):-
	D1 is D + 1,
	P1y is Py +1,
	P = Px:P1y,
      	P \== Px : Py,	% Must have moved
	WP = Px:Py,
	not inway( WP, W, P ), 	% white king not in way
	not inway( Px : Py, W, P ), 	% white king not in way
	not inway( (Px : Py), B, P ). 	% black king not in way

move( legal, us..P, M, P1 ) :-
	(
		MC = kingdiagfirst
	;
		MC = pawnmove
	),
	move( MC, us..P, M, P1 ).

pawnlost( _.._W..B..B.._ ,_).	% pawn has been captured

pawnlost( them..W..P..B.._ ,_) :-
	ngb( B, P ),	% black king attacks pawn
	not ngb( W, P ).% white king does not defend

safe_distance(_..W..P..B.._,_) :-
	dist(W, P, D1),
	dist(B, P, D2),
	D1 < D2.

out_of_square(_..W..P..B.._,_) :-
	dist(W, P, D),
	D > 1.

pawn_free( _..W..Px:Py..B.._,_) :-
	P8 = Px:8,
	dist( P8, Px:Py, D1),
	dist( P8, B, D2),
	D1 < D2.

pawnexposed( Side..W..P..B.._D, _ ) :-
	dist( W, P, D1 ),
	dist( B, P, D2 ),
	(
		Side = us, !, 
      		D1 > D2 + 1
	;
		Side = them, !,
		D1 > D2
	).

pawn_path_held(_..Wx:Wy..Px:Py..B.._,_..W..P.._.._) :-
	dist(Px:Wy, Wx:Wy, WP),
	WP < 2.

kingdivides( Side..Wx:Wy..Px:Py..Bx:By..D,_) :-
	%write('		TEST TEST TEST TEST :	'),write(Wx:Wy),write(Px:Py),write(Bx:By),
	((Wx >= Px, Wx =< Bx)
	;
	(Wx =< Px, Wx >= Bx))
	;
	((Wy >= Py, Wy =< By)
	;
	(Wy =< Py, Wy >= By)).
%(Px < Wx, Px < Bx ;
%	Px > Wx, Px > Bx),
%	(Py < Wy, Py < By ;
%	Py > Wy, Py > By).

kingadvances( Side.._:Wy..P..B..D, _.._:Wy2..P..B.._) :-
	%Wx2 is Wx +1,
	%Wy2 is Wy -1,
	%W = Wx2:Wy2,
	Y is Wy -1,
	Wy2 = Y.
	%write('	TEST TEST TEST A: '),write(Wx:Wy),
	%write('	TEST TEST TEST B: '),write(W).

pawnleading( Side..Wx:Wy..Px:Py..B..D,_) :-
	Py > Wy.

pawnprotected(Side..Wx:Wy..Px:Py..B..D,_) :-
	Py =< Wy,
	(Px = Wx+1 ; Px = Wx-1).

did_not_move_pawn( Size.._W..P.._B.._D, Size.._W..P.._B.._D).

did_not_move_king( Size..W.._P.._B.._D, Size..W.._P.._B.._D).
	%W2 /= Wx:Wy,
	%write(' W '), write(Wx:Wy),
	%write(' W2: '),write(W2),_D2 is _D+1,
	%write(' D2: '),write(_D2)
	

	