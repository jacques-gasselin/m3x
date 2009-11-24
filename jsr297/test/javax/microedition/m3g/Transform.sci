function [s]=getScale(x, y, z)
  s = eye(4, 4)
  s(1, 1) = x
  s(2, 2) = y
  s(3, 3) = z
endfunction

function [s]=getTranslate(x, y, z)
  s = eye(4, 4)
  s(1, 4) = x
  s(2, 4) = y
  s(3, 4) = z
endfunction

function [o]=getRotate(degrees, x, y ,z)
  //normalize the vector
  v = [x, y, z]
  v = v / norm(v)
  x = v(1)
  y = v(2)
  z = v(3)
  //convert the angle to radians for trig
  angle = %pi * degrees * (1 / 180.0)
  c = cos(angle)
  s = sin(angle)
  //construct the rotation matrix
  o = [  x*x*(1-c)+c,   x*y*(1-c)-z*s,  x*z*(1-c)+y*s, 0,
       x*y*(1-c)+z*s,     y*y*(1-c)+c,  y*z*(1-c)-x*s, 0,
       x*z*(1-c)-y*s,   y*z*(1-c)+x*s,    z*z*(1-c)+c, 0,
                   0,               0,              0, 1]
endfunction
      
function [mscaled]=postScale(m, x, y, z)
  mscaled = m * getScale(x, y, z)
endfunction

function [mtranslated]=postTranslate(m, x, y, z)
  mtranslated = m * getTranslate(x, y, z)
endfunction

function [mrotated]=postRotate(m, degrees, x, y, z)
  mrotated = m * getRotate(degrees, x, y, z)
endfunction
    