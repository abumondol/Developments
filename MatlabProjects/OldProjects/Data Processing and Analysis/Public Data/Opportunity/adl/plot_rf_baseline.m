prf = [];
for i=1:15
    prf = [prf; bres_loso(i).PR,bres_loso(i).RC,bres_loso(i).F1];
end


