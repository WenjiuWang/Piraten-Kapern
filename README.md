Wenjiu Wang
0300174736

This is the source code for COMP5104 A1. A networked, 3 player Piraten Kapern game.

## How to play (Normal game)

1) Import this project into eclipse
2) Run Game server (Game.java).
3) Add 3 players (Player.java), enter name for each player.
4) Type then hit enter when asked to provide input.
5) Play until game ends.

## How to test

1) Run the test suite or any test cases as Junit test.

## About my testing implementation

Each test cases for the correction grid is implemented to a networked full game consisting 3 players. All the user inputs are pre-determined and the whole game process is automated. 

This is achieved by testing-purpose only classes: RiggedGame and RiggedPlayer.

Those two classes are inherited from class Game and Player.
- RiggedGame: everything is same as Game except all random events are replaced by pre-determined values provided when construct this class.
    
- RiggedPlayer: everything is same as Player except all player inputs are directly from pre-dtermined values provided when construct this class.
