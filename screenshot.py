import win32gui, win32ui, win32con, win32api, ctypes
import numpy as np

ctypes.windll.shcore.SetProcessDpiAwareness(2)

def screenshot():
  # Set up
  w = win32api.GetSystemMetrics(0)
  h = win32api.GetSystemMetrics(1)
  hwnd = win32gui.GetDesktopWindow()
  wDC = win32gui.GetWindowDC(hwnd)
  dcObj = win32ui.CreateDCFromHandle(wDC)
  cDC=dcObj.CreateCompatibleDC()
  dataBitMap = win32ui.CreateBitmap()
  dataBitMap.CreateCompatibleBitmap(dcObj, w, h)
  cDC.SelectObject(dataBitMap)
  cDC.BitBlt((0,0), (w, h), dcObj, (0,0), win32con.SRCCOPY)
  # Get the image string
  imstr = dataBitMap.GetBitmapBits(True)
  # Free Resources
  dcObj.DeleteDC()
  cDC.DeleteDC()
  win32gui.ReleaseDC(hwnd, wDC)
  win32gui.DeleteObject(dataBitMap.GetHandle())
  # Convert to numpy and return
  return np.frombuffer(imstr, dtype=np.uint32).reshape((h, w)) - 0xff000000