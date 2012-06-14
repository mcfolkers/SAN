% -*- Mode: Prolog -*-
% $Id$
% from Prolog Programming for AI 3d edition, Bratko p. 597

% A miniature implementation of Advice Language 0
% 
% this program plays a game from a given starting position using knowledge
% represented in Advice Language 0

% initialize the operators needed for the advice language

% TODO: We should be able to load an already existing forcing tree. 
% play a game starting from Pos
playgame( Pos ):-
	% try to load existing forcing tree from file.
	loadForcingTree( ForcingTree ),
  nonvar(ForcingTree),
	playgame( Pos, ForcingTree ).	% start with the previous forcing tree


% continue game from Pos with the given forcing tree ( may be nil )
playgame( Pos, ForcingTrees ) :-
  %show( Pos ), nl,	% display current position, can be usefull for debuging
	% end of game
	( end_of_game( Pos ),	% check for end of game
	  write( 'End of Game' ), nl, !
	; % or continue and play next move
    bk(Pos, BK),
    subtree(ForcingTrees, _-BK, ForcingTreeForThemMove),
    write('ForcingTreeForThemMove = '), write(ForcingTreeForThemMove), nl,

    playmove( Pos, ForcingTreeForThemMove, _Pos1, ForcingTree1 ), !,
    % Just play one move, show Pos at end, Matthijs
    
    % Prolog is only called for one move, so do not continue playing:
    %playgame( Pos1, ForcingTree1 )	% continue game
    % Rather save the forcing tree to a file so to remember it for
    % the next move:
    saveForcingTree( ForcingTree1 )
	).
  %show( Pos1 ).% display current position, can be usefull for debuging

% Play us move according to forcing tree
playmove( Pos, Move..Ftree1, Pos1, Ftree1 ) :-
	side( Pos, us ), % its our turn to move
	% check if this is a valid move (but what if it is not?)
	% and evaluate the next position
	write( 'Making move: ' ), write( Pos ), write( ' -> '), write( Move ), nl,
	legalmove( Pos, Move, Pos1 ), 
	write( 'OK, new pos: ' ), write( Pos1 ), nl,
	savemove( Move ).

% Depricated:
% read them move
%playmove( Pos, Ftree, Pos1, Ftree1 ) :-
%	side( Pos, them ), % their turn to move
%	write( 'What is your next move? (format is 3:3-3:4.)  ' ), 
%	read( Move ), nl,
%	(legalmove( Pos, Move, Pos1 ),	 % check if this is a valid move
%	  subtree( Ftree, Move, Ftree1 ) % get the forcing tree associated with this move
%	  ;
%	  write( 'illegal move' ), nl,
%	  playmove( Pos, Ftree, Pos1, Ftree1 )
%	).

% if current forcing tree is empty ( nil )
playmove( Pos, nil, Pos1, Ftree1 ) :-
	side( Pos, us ),		% our turn to move
	resetdepth( Pos, Pos0 ), 	% Pos0 = Pos with depth 0
	strategy( Pos0, Ftree0 ), !, 	% construct new forcing tree
	write( 'New tree: ' ), write( Ftree0 ), nl,
	playmove( Pos0, Ftree0, Pos1, Ftree1 ).	% we make our move

playmove(_, _, _, _) :-
  write( 'Prolog endgame solver failed to find move' ), nl.

% select a forcing sub tree that corresponds to the move
% this move is inside the forcing tree
subtree( FTrees, Move, Ftree ) :-
	member( Move..Ftree, FTrees ), !.
	
% else return an empty sub tree (nil)
subtree( _Ftrees, _Move, nil ).

% construct a new forcing tree from this position
strategy( Pos, FTree ) :-
	Rule :: if Condition then AdviceList,	% consult advice table
	holds( Condition, Pos, _ ),		% match Pos against pre condition
	member( AdviceName, AdviceList ),	% try advices in turn
	nl, write( 'Trying advice: ' ), write( AdviceName ), nl,
	satisfiable( AdviceName, Pos, FTree ), !, % Satisfy AdviceName in Pos
        nl, % display clearly which advice will be used
        write('----------------------------------'), nl,
        write('Rule: '), write(Rule), nl,
        write('Accepted advice: '), write( AdviceName ), nl,
        write('----------------------------------'), nl.

% Satisfy AdviceName in Pos
satisfiable( AdviceName, Pos, FTree ) :-
	advice( AdviceName, Advice ),   % retreive piece of advice
	sat( Advice, Pos, Pos, FTree ).	% sat needs 2 positions for
					% comparison predicates

% satisfy an holding goal on this position
sat( Advice, Pos, RootPos, FTree ) :-
	holdinggoal( Advice, HG ), 	% retreive holding goal
	write( ' Holding goal:  '  ), write( HG ), nl,
	holds( HG, Pos, RootPos ), 	% holding goal satisfied
	write( 'Holding goal satisfied: ' ), write( HG ), nl,
	sat1( Advice, Pos, RootPos, FTree ).

% satisfy better goal
sat1( Advice, Pos, RootPos, nil ) :-
	bettergoal( Advice, BG ), 	% retreive better goal
  % nl,write( ' Better goal:  '  ), write( BG ), nl,write( ' RootPos:  '  ), write( RootPos ),nl,write( ' Pos:  '  ), write( Pos ),nl,
	holds( BG, Pos, RootPos ),	% better goal satisfied
	write( 'Better goal satisfied: ' ), write( BG ), nl,
        !.

sat1( Advice, Pos, RootPos, Move..Ftrees ) :-
	side( Pos, us ),		% our turn to move
	write( 'We move: ' ),
	usmoveconstr( Advice, UMC ),	% check the movement constraint for this advice
	write( ' constraint: ' ), write( UMC ), nl,
	moveC( UMC, Pos, Move, Pos1 ),	% find a move that satisfies the move constraint
	write( 'Move:  ' ), write( Move ), nl,
	sat( Advice, Pos1, RootPos, Ftrees ).

sat1( Advice, Pos, RootPos, FTrees ) :-
	side( Pos, them ),		% their turn to move
	themmoveconstr( Advice, TMC ),	% check the movement constraint for this advice
	% find all moves that satisfies the move constraint
	bagof( Move..Pos1, moveC( TMC, Pos, Move, Pos1 ), MPList ),	
	satall( Advice, MPList, RootPos, FTrees ).

% satisfy all positions
satall( _, [], _, [] ).

% walk through all possible moves
satall( Advice, [ Move..Pos | MPList ], RootPos, [ Move..FTree | MFTs ] ) :-
	sat( Advice, Pos, RootPos, FTree ),
	satall( Advice, MPList, RootPos, MFTs ).

% check if a goal holds for a position

% goal with and: check if both goals are satisfied
holds( Goal1 and Goal2, Pos, RootPos ) :- 
	!,	
	holds( Goal1, Pos, RootPos ),
	holds( Goal2, Pos, RootPos ).

% goal with or: check if at least one is satisfied
holds( Goal1 or Goal2, Pos, RootPos ) :- 
	!,
	( holds( Goal1, Pos, RootPos )
	  ;
	  holds( Goal2, Pos, RootPos )
	).

holds( not Goal, Pos, RootPos ) :-
	!,
	not holds( Goal, Pos, RootPos ).

%holds( Pred, Pos, RootPos ) :-
%	Cond =.. [ Pred, Pos ],
%	call( Cond ).

holds( Pred, Pos, RootPos ) :-
	Cond =.. [ Pred, Pos, RootPos ],
	call( Cond ).

%holds( Pred, Pos, RootPos ) :-
%        ( Cond=.. [ Pred, Pos ]
%      ;
%        Cond =.. [ Pred, Pos, RootPos ] ),
%        call(Cond).

% interpreting move constraints

% and -> satisfy both
moveC( MC1 and MC2, Pos, Move, Pos1 ) :-
	!,
	%write( ' AND found ' ), nl, write( MC1 ), nl, write( MC2 ), nl,	
	moveC( MC1, Pos, Move, Pos1 ),
	moveC( MC2, Pos, Move, Pos1 ).

% or -> satisfy one
moveC( MC1 or MC2, Pos, Move, Pos1 ) :-
	!,
	( moveC( MC1, Pos, Move, Pos1 )
	  ;
	  moveC( MC2, Pos, Move, Pos1 )
	).

% then -> satisfy one
moveC( MC1 then MC2, Pos, Move, Pos1 ) :-
	%write( ' THEN found ' ), nl, write( MC1 ), nl, write( MC2 ), nl,
	!,
	( moveC( MC1, Pos, Move, Pos1 )
	  ;
	  moveC( MC2, Pos, Move, Pos1 )
	).

moveC( MC, Pos, Move, Pos1 ) :-
	move( MC, Pos, Move, Pos1 ).

% selectors for components of piece-of-advice
bettergoal( BG : _, BG ).

holdinggoal( _BG : HG : _, HG ).

usmoveconstr( _BG : _HG : UMC : _, UMC ).

themmoveconstr( _BG : _HG : _UMC : TMC, TMC ).

% Save a forcing tree to file
saveForcingTree(ForcingTree) :-
	tell('forcingTree.pl'),
	write_term(ForcingTree, []),
	write('.'),
	told.

% Load a forcing tree from file
loadForcingTree(ForcingTree) :-
	FileName = 'forcingTree.pl',
	exists_file(FileName), !,
	see(FileName),
	read_term(ForcingTree, []),
	seen,
	write('loaded '),
	write(FileName), 
	write(' :  '),
	write(ForcingTree), nl.

loadForcingTree(nil) :-
	FileName = 'forcingTree.pl',
	write('file '),
	write(FileName),
	write(' does not exist'), nl.

