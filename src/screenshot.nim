## Screenshots

import winim

# Captures the rectangle beteen the two given points and returns the raw image data
proc screenshot*(x1, y1, x2, y2: int32): seq[uint8] =
  let # Get rectangle dimensions
    w = abs(y2 - y1)
    h = abs(x2 - x1)
  var # Capture bitmap
    hScreen = GetDC(cast[HWND](nil))
    hDC = CreateCompatibleDC(hScreen)
    hBitmap = CreateCompatibleBitmap(hScreen, w, h)
  discard SelectObject(hDC, hBitmap)
  discard BitBlt(hDC, 0, 0, w, h, hScreen, min(x1, x2), min(y1, y2), SRCCOPY)

  var mybmi: BITMAPINFO # Bit map info
  mybmi.bmiHeader.biSize = int32 sizeof(mybmi)
  mybmi.bmiHeader.biWidth = w
  mybmi.bmiHeader.biHeight = h
  mybmi.bmiHeader.biPlanes = 1
  mybmi.bmiHeader.biBitCount = 24
  mybmi.bmiHeader.biCompression = BI_RGB
  mybmi.bmiHeader.biSizeImage = w * h * 3
  
  result = newSeq[uint8](w * h * 3)
  # Feed image data into the result variable
  discard CreateDIBSection(hdc, addr mybmi, DIB_RGB_COLORS, cast[ptr pointer](unsafeAddr(result[0])), 0, 0)
  discard GetDIBits(hdc, hBitmap, 0, h, cast[ptr pointer](unsafeAddr(result[0])), addr mybmi, DIB_RGB_COLORS)