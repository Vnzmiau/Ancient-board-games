# Senet Game - Complete Rules Implementation

## Game Overview
A complete implementation of the ancient Egyptian board game Senet with all historical rules enforced.

## Board Layout
- **30 Squares** arranged in 3 rows of 10
- **Pieces move** from square 1 to square 30
- **Row 1** (squares 1-10): Starting position for pieces
- **Row 2** (squares 11-20): Middle progression
- **Row 3** (squares 21-30): Final progression toward bearing off

## Special Squares
- **Square 15**: Destination for water trap (square 27)
- **Square 26, 28, 29**: Safe squares (cannot be attacked)
- **Square 27**: Water trap - piece redirects to square 15 (or nearest available square going backward)

## Game Start
1. Both players roll dice until someone rolls a **1**
2. Player who rolls 1 becomes the **dark piece owner** and takes control of dark pieces
3. That player must move the dark piece on square 10 forward 1 square
4. That player gets to roll again (because they rolled 1)

## Turn Mechanics

### Rolling the Dice
- Players roll 4 dice sticks (two-sided)
- **Flat up** = 1 point, **Rounded up** = 0 points
- **All 4 rounded up** = 6 points (special case)
- **Roll result**: 1, 2, 3, 4, or 6 points

### Roll Effects
- **Roll 1, 4, or 6**: Player rolls again immediately
- **Roll 2 or 3**: Turn ends, next player rolls

## Piece Movement

### Basic Movement
- Move exactly the number rolled (no more, no less)
- Players can select any piece they control to move
- Valid destination must be unblocked and within rules

### Forward vs Backward Movement
1. **Forward Movement** (preferred): Move forward by roll amount
2. **Backward Movement** (only if blocked): Move backward by roll amount if forward is blocked
   - Rule: *"If you are unable to move a piece forward, then you must move a piece in reverse"*

### Movement Validation

#### Cannot Move To:
- A square occupied by your own piece (friendly occupied square)
- A square that would land on opponent's 3-piece block
- A square passing through opponent's 3-piece block

#### Capturing Rules:
- **Landing on opponent piece**: Swap positions (move opponent to your starting square)
- **Cannot capture on safe squares** (26, 28, 29)
- **Cannot capture protected groups**:
  - 2 consecutive pieces of same color protect each other
  - 3+ consecutive pieces form a block
  - Opponent cannot attack or pass over blocks

#### No Valid Moves:
- If a player has no valid moves in any direction: **Skip turn** (unless rolling 1, 4, or 6)

## Bearing Off

### Rules:
- **Only allowed** when all your pieces have left Row 1 (squares 1-10)
- When roll would move piece past square 30: Remove piece from board
- If roll exceeds distance to square 30:
  - Move another piece with the remainder
  - Example: Piece at square 28, roll 4 → bear off piece, remaining 2 moves another piece

### Victory:
- **First player** to bear off all 5 pieces wins

## Implementation Details

### Classes
- **SenetGame**: Game state management, rule validation, turn tracking
- **SenetBoard**: Board state, piece positions, move validation
- **SenetPiece**: Individual piece with color and position
- **SenetScreen**: UI with piece display, move highlighting, game status

### Movement Validation Checklist
✓ Cannot move onto friendly pieces  
✓ Cannot land on opponent 3-piece blocks  
✓ Cannot pass through opponent blocks  
✓ Cannot capture protected pieces (2+)  
✓ Cannot capture on safe squares  
✓ Water trap redirects from square 27 to square 15  
✓ Backward movement only when forward blocked  
✓ Bearing off only when Row 1 is clear  
✓ Roll again on 1, 4, or 6  
✓ End turn on 2 or 3  

### User Interface
- **Player Indicator**: Shows "Player 1's Turn" or "Player 2's Turn"
- **Game Status**: Shows current roll, dark piece owner, valid actions
- **Piece Selection**: Click piece to select (if valid moves exist)
- **Move Highlighting**: Gold-bordered squares show valid destinations
- **Piece Highlight**: Selected piece gets yellow glow effect
- **Dice Rolling**: Click dice to roll (disabled during move pending)

## Gameplay Flow
1. Both players roll to find dark piece owner
2. Dark piece owner rolls and moves dark piece on square 10 forward 1
3. Normal turns begin with dice roll
4. Player selects piece and clicks valid destination
5. Move executes, captures handled, turn ends
6. Check if should roll again (1, 4, or 6)
7. First to bear off all pieces wins!
