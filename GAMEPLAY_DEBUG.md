# Senet Gameplay Debug Guide

## Expected Gameplay Flow

### Phase 1: Initial Roll (Find Dark Piece Owner)
1. **UI Shows**: "Rolling to find dark piece owner... Player 1 - Roll the dice!"
2. **Action**: Click dice to roll
3. **Result**: Dice show 1-6 flat sides, Player 1 or Player 2 indicator updates
4. **Loop**: Repeat until someone rolls a 1

### Phase 2: Game Start (First Move of Dark Piece Owner)
1. **UI Shows**: Roll number and "Dark: Player X"
2. **Auto-move**: The dark piece on square 10 moves forward 1 square automatically
3. **Turn Check**: If roll was 1 (which it was), player gets to roll again

### Phase 3: Normal Gameplay
1. **Current Player**: "Player X's Turn" - only this player's pieces can be moved
2. **Roll Dice**: Click to roll (1, 2, 3, 4, or 6 pips)
3. **UI Updates**: "Roll: X | Dark: Player Y | Select a piece!"
4. **Select Piece**: Click on your piece (should have yellow glow when selected)
5. **Valid Squares**: Valid destination squares highlight in gold
6. **Move**: Click on gold square to move piece there
7. **Results**:
   - Piece moves to new square
   - Dice unlock automatically
   - If captured an opponent piece, it swaps back to attacker's starting square
   - If piece lands on square 27 (water), it goes to square 15
   - UI updates to show next action

### Phase 4: Turn End
- **Roll 1, 4, or 6**: "Roll again!" - same player rolls again
- **Roll 2 or 3**: "Next player's turn" - turn passes to other player
- **No valid moves**: Skip turn automatically

### Phase 5: Bearing Off
- Only allowed when all pieces are out of squares 1-10
- When bearing off successfully, piece disappears from board
- Extra roll amount (if roll > distance to end) moves another piece

### Phase 6: Win
- "🎉 Player X WINS! 🎉"
- Game locks (cannot roll or move)

## Debugging Checklist

✓ Dice roll when clicked
✓ Dice display updates (flat/rounded sides)
✓ Player indicator changes
✓ Pieces are visible on board
✓ Pieces highlight when clicked
✓ Valid squares show in gold when piece selected
✓ Piece moves to highlighted square when clicked
✓ Captured pieces swap positions
✓ Water trap redirects correctly
✓ Bearing off works
✓ Game declares winner

## Click Handler Flow

1. User clicks piece -> `handlePieceClick(piece, visual)`
2. Validation checks (game state, current player, valid moves)
3. Highlight piece with yellow glow
4. Call `showValidMoves(piece)` -> highlights valid destination squares
5. User clicks gold square -> `highlightSquare()` click handler fires
6. Calls `executeMove(piece)`
7. Updates all piece visuals via `updatePieceVisual()`
8. Re-enables dice
9. Updates game status display
