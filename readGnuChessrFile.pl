% -*- Mode: Prolog -*-
% $Id: readGnuChessrFile.pl,v 1.5 2008/06/06 08:34:26 obooij Exp $
% obooij

% Read a chess-game description file as it is saved and loaded by gnuchessr.
% It ignores all the chess-game relevant stuff, except the bord-setting.
% TODO: not ignore this other info, like who's turn it is.
% The bord representation is used as is described in Bratko (the Advice
% Language-section. It only permits 3 pieces a black king, a white king and a
% second white piece (in Bratko a rook). So it is assumed a board file is given
% with limited to such a setting.

% readGnuChessrFile( -Position )
% read a 'board.gch'.
readGnuChessrFile( Position ) :-
  FileName = 'board.gch',
  readGnuChessrFile( FileName, Position ).

% readGnuChessrFile( +FileName, -Position )
% read a file.
readGnuChessrFile( FileName, Position ) :-
  open( FileName, read, GameStream  ),
  readHeader( GameStream ),
  readPosition( GameStream, 1:8, Position ),
  close(GameStream).

% readHeader( +GameStream )
% the header of the file is ignored (for now).
% this predicate reads up until the start of the chessboard-position 1:8
% BUG: After sourcing chess.pl, read_line_to_codes gives Errors when the
% operator : is being used. And it is being used by AL0.pl and others.
%readHeader( GameStream ) :-
%  %	get_byte( GameStream, _), 
%  %  read_line_to_codes( GameStream, _ ),
%  %  read_line_to_codes( GameStream, _ ),
%  %  read_line_to_codes( GameStream, _ ),
%  %  read_line_to_codes( GameStream, _ ),
%  %  read_line_to_codes( GameStream, _ ),
%  	get_byte( GameStream, _), 
%  	get_byte( GameStream, _),
%  	get_byte( GameStream, _).

readHeader( GameStream ) :-
  readIgnoreChars(GameStream, 138).


% readIgnoreChars( +GameStream, +NrOfChars )
% Read NrOfChars chars and ignore them.
readIgnoreChars(_GameStream, 0).

readIgnoreChars(GameStream, NrOfChars) :-
	get_byte( GameStream, _Char),
  %write(NrOfChars),write(" ") , %DEBUG
  %put( Char ), nl, %DEBUG
  NrOfCharsLeft is NrOfChars -1,
  readIgnoreChars(GameStream, NrOfCharsLeft).


% readPosition( +GameStream, +X:Y, -Position )
% read one character and if it's a piece use the coordinates
% X:Y to put it on the board-representation (Position).
% end at end of the board
readPosition( _, nil:nil, _Position ):- !.

readPosition( GameStream, X:Y, Position ):-
	get_byte( GameStream, Char ),
  %put( Char ), put( '<' ), nl, %DEBUG
  %write(Char), nl, %DEBUG
  readChar( GameStream, Char, X:Y, Position, X2:Y2 ),
	readPosition( GameStream, X2:Y2, Position ).

% readChar( +GameStream, +Char , +X:+Y, -Position, -X2:-Y2 )
% process one Char. 

% 32 = ' ', if it's a space then we are at the end of a row.
% We then skip the rest of the row (the 0's) and read up until 
% the beginning of the next row.
readChar( GameStream, 32 , X:Y, _Position, X:Y ) :- !,
  readIgnoreChars(GameStream, 18).

% 46 = '.', an empty square, ignore it
readChar( _GameStream, 46, X:Y, _Position, X2:Y2 ) :- !,
  next( X:Y, X2:Y2 ).

% 75 = 'K', put a black king to the board 
readChar( _GameStream, 75 , X:Y, us.._.._..X:Y..0, X2:Y2 ) :- !,
  next( X:Y, X2:Y2 ).

% 107 = 'k', put a white king to the board 
readChar( _GameStream, 107, Pos, us..Pos.._.._..0, Pos2 ) :- !,
  next( Pos, Pos2 ).

% Put an extra white piece on the board
readChar( _GameStream, Char, X:Y, us.._..Xcheck:Y.._..0, X2:Y2 ) :-
  extraPiece(Char),
  % Check if the extra-piece is not already instantiated. If so, then put the
  % piece on the spot of the white king.
  var(Xcheck), !, 
  Xcheck = X,
  next( X:Y, X2:Y2 ).

% Put an extra-extra white piece on the board (in case of a no-king-endgame eg.
% rook+rook vs king)
readChar( _GameStream, Char, X:Y, us..Xcheck:Y.._.._..0, X2:Y2 ) :-
  extraPiece(Char),
  % Check if the extra-piece is not already instantiated. Just an error-check
  % to see if more than 3 pieces are placed on the board.
  var(Xcheck), !, 
  Xcheck = X,
  next( X:Y, X2:Y2 ).

% produce error if the end of file is reached without without a complete board
readChar( _GameStream, -1, _X:_Y, us.._.._.._..0, nil:nil ):- !, 
	write( 'WARNING: end of file reached, board.gch is not valid ' ), nl,
	!.

% give a warning if other characters are found or more than one extra-piece.
readChar( _GameStream, Char, X:Y, _, X2:Y2  ) :-
	write( 'WARNING: Do not know what to do with \'' ),
  put(Char), 
	write( '\' in board.gch. Ignoring it' ),
  nl,
  write(X), write(':'), write(Y), nl, %DEBUG
  next( X:Y, X2:Y2 ).

% extraPiece(+Char)
% Define the possible second white piece.
% This depends on the "game-mode" defined in KRPLrook, KRPL...
extraPiece( 114 ) :- % 'r' rook
  (mode(rook); mode(rookrook)), !.

extraPiece( 113 ) :- % 'q' queen
  mode(queen).

extraPiece( 112 ) :- % 'p' pawn
  mode(pawn).


% get next position on the board
% 8:8 finish
next( 8:1, nil:nil ):- !.

% end of a line
next( 8:Y, 1:Y2 ):- !,
	Y2 is Y - 1.

% middle of line
next( X:Y, X2:Y ):- !,
	X2 is X + 1.
