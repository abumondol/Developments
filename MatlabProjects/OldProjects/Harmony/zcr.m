function res = zcr(data)
res= mean( abs( diff( sign(data) ) ) );