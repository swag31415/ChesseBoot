# chess-bot
# A pretty decent chessBot that can recognize and play chess games on pc

from screenshot import screenshot
import numpy as np
from stockfish import Stockfish, StockfishException
import matplotlib.pyplot as plt
from matplotlib import animation

# in ARGB form
light = 0x00eeeed2
dark = 0x00769656

exclude = [light, dark, 0x00edeed1, 0x00779952, 0x00baca2b, 0x00f6f669]

X = []
mmap = {l:i for s in ['12345678','abcdefgh'] for i, l in enumerate(s)}

# assuming player is white
black_set = [None, None, None, 'q', 'k', 'b', 'n', 'r', 'p']
white_set = ['P', 'R', 'N', 'B', 'Q', 'K', None, None, None]
space = [None] * (4*8 + 2*7)

def find_board():
  im = screenshot()
  ys, xs = np.nonzero((im == light) | (im == dark))
  if xs.size == 0 or ys.size == 0:
    print('no chess board')
    exit()
  return np.min(xs), np.min(ys), np.max(xs), np.max(ys)

def get_board(rect):
  im = screenshot()
  x0, y0, x1, y1 = rect
  return im[y0:y1, x0:x1]

def get_is_white(board):
  w, h = board.shape
  return np.average(board[:h//2]) < np.average(board[h//2:])

def index_board(board, i, j):
  w, h = board.shape
  cw, ch = w/8, h/8
  xi, yi = int(cw * j), int(ch * i)
  return board[yi:yi+int(ch), xi:xi+int(cw)]

def get_pieces(board, is_white):
  piece_map = {}
  layout = black_set + space + white_set
  if not is_white:
    layout = white_set[::-1] + space + black_set[::-1]
  for i, p in enumerate(layout):
    if p == None: continue
    tile = index_board(board, i // 8, i % 8)
    mask = np.isin(tile, exclude, invert=True)
    piece_map[p] = np.where(mask, tile, 0)
  return piece_map

def get_layout(board, piece_map):
  layout = ''
  for i in range(0, 8):
    for j in range(0, 8):
      tile = index_board(board, i, j)
      mask = np.isin(tile, exclude, invert=True)
      if not np.any(mask):
        layout += ' '
        continue
      cand = np.where(mask, tile, 0).astype(np.int64)
      mdiff = np.sum(cand)
      best = ' '
      for p, ref in piece_map.items():
        diff = np.sum(np.abs(cand - ref.astype(np.int64)))
        if diff < mdiff:
          mdiff = diff
          best = p
      layout += best
  return layout

def make_fen(layout, is_white):
  fen = ''
  empties = 0
  for i, c in enumerate(layout):
    if i > 0 and i % 8 == 0:
      if empties > 0:
        fen += str(empties)
        empties = 0
      fen += '/'
    if c == ' ':
      empties += 1
    else:
      if empties > 0:
        fen += str(empties)
        empties = 0
      fen += c
  if not is_white:
    fen = fen[::-1]
  fen += ' w' if is_white else ' b'
  fen += ' - - 0 1'
  return fen

sf_path = "C:\\Users\\kidsw\\Documents\\stockfish\\stockfish.exe"
params = {
  'Hash': 512,
  'Skill Level': 20,
  'Threads': 4
}
stockfish = Stockfish(path=sf_path, parameters=params)

board_loc = find_board()
im = get_board(board_loc)
w, h = im.shape

fig, ax = plt.subplots()
disp = ax.imshow(im, animated=True)
ax.arrow(0, 0, w, h, width=100, length_includes_head=True)

is_white = get_is_white(im)
piece_map = get_pieces(im, is_white)
last_fen = ''
def update_image(i):
  global last_fen, stockfish
  im = get_board(board_loc)
  layout = get_layout(im, piece_map)
  fen = make_fen(layout, is_white)
  if fen == last_fen:
    return None
  last_fen = fen
  disp.set_array(im)
  try:
    stockfish.set_fen_position(fen)
    move = stockfish.get_best_move()
    if move:
      x0, y0, xi, yi = [(mmap[m]+0.5)/8 for m in move]
      if not is_white:
        x0, y0, xi, yi = 1-x0, 1-y0, 1-xi, 1-yi
      y0, yi = 1-y0, 1-yi
      x0, y0, xi, yi = x0*w, y0*h, (xi-x0)*w, (yi-y0)*h
      ax.patches.pop(0)
      ax.arrow(x0, y0, xi, yi, width=w/30, head_width=w/12, head_length=w/12, length_includes_head=True)
  except StockfishException:
    stockfish = Stockfish(path=sf_path, parameters=params)
    return None

ani = animation.FuncAnimation(fig, update_image, interval=250)
plt.axis('off')
plt.show()