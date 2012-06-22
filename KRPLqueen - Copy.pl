% -*- Mode: Prolog -*-

% Position is represented by Side..Wx : Wy..Qx : Qy .. Bx : By .. Depth
% Side is side to move next ( us or them )
% Wx, Wy are X and Y coordinates of the white king
% Qx, Qy are X and Y coordinates of the white queen
% Bx, By are the X and Y coordinates of the black king
% depth is depth of position in the search tree

mode(queen).

% call the general original move predicates for queen moves etc.
move(A,B,C,D):-
        moveGeneral(A,B,C,D).

