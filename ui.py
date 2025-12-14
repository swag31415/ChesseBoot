import tkinter as tk

class BoardFrame(tk.Frame):
  def __init__(self, width=400, light="#292e30", dark="#0e1011", piece_color='#dad6d0', arrow_color='#bb86fc'):
    super().__init__()
    self.width = width
    self.fw = width / 8
    self.light = light
    self.dark = dark
    self.piece_color = piece_color
    self.arrow_color = arrow_color
    self.charmap = {
      'K':'♚',
      'Q':'♛',
      'R':'♜',
      'B':'♝',
      'N':'♞',
      'P':'♟',
      'k':'♔',
      'q':'♕',
      'r':'♖',
      'b':'♗',
      'n':'♘',
      'p':'♙',
      ' ':' ',
    }
    self.initUI()
  def initUI(self):
    self.master.title("Board")
    self.pack(fill=tk.BOTH, expand=1)
    self.canvas = tk.Canvas(self)
    self.ids = [[None for j in range(0, 8)] for i in range(0, 8)]
    layout = [[' ' for j in range(0, 8)] for i in range(0, 8)]
    for i in range(0, 8):
      for j in range(0, 8):
        col = self.light if (i+j) % 2 == 0 else self.dark
        self.canvas.create_rectangle(j*self.fw, i*self.fw, (j+1)*self.fw, (i+1)*self.fw, outline=col, fill=col)
        self.ids[i][j] = self.canvas.create_text((j+.5)*self.fw, (i+.5)*self.fw, text=self.charmap[layout[i][j]], fill=self.piece_color, font=('Arial 22'))
    self.arrow = self.canvas.create_line(0, 0, 0, 0, arrow=tk.LAST, fill=self.arrow_color)
    self.canvas.pack(fill=tk.BOTH, expand=1)
  def set_layout(self, layout, arrow):
    for i in range(0, 8):
      for j in range(0, 8):
        self.canvas.itemconfig(self.ids[i][j], text=self.charmap[layout[i][j]])
    x0, y0, xi, yi = [c * self.width for c in arrow]
    self.canvas.coords(self.arrow, x0, y0, xi, yi)

def init_ui():
  bgc = '#000'
  fgc = '#fff'
  font = 'Trebuchet 12'
  root = tk.Tk(className='Chess-Bot')
  root.configure(bg=bgc)
  root.geometry("400x440")
  root.resizable(False,False)
  root.attributes('-topmost',True)
  bf = BoardFrame()
  reset_btn = tk.Button(root, text='reset', font=font, bg=bgc, fg=fgc)
  pause_btn = tk.Button(root, text='start', font=font, bg=bgc, fg=fgc)
  quick_btn = tk.Button(root, text='quick', font=font, bg=bgc, fg=fgc)
  reset_btn.pack(side=tk.LEFT)
  pause_btn.pack(side=tk.LEFT)
  quick_btn.pack(side=tk.LEFT)
  def update_ui(layout, arrow):
    bf.set_layout(layout, arrow)
  def loop(dt, main_func, reset_func, pause_func, quick_func):
    reset_btn.configure(command=reset_func)
    def pause_wrapper():
      if pause_btn['text'] == 'start':
        reset_func()
      pause_btn.configure(text='unpause' if pause_btn['text'] == 'pause' else 'pause')
      pause_func()
    pause_btn.configure(command=pause_wrapper)
    quick_btn.configure(command=quick_func)
    def task():
      main_func()
      root.after(dt, task)
    task()
    root.mainloop()
  return update_ui, loop