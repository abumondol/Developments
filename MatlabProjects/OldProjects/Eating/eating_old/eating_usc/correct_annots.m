function annots = correct_annots(annots, acr)    
    acr_len = size(acr, 1);    
    for i=1:acr_len        
        a = acr(i,4);
        b = annots(acr(i,3), 1);
        if  abs(a-b) > 0.5 
            fprintf('===== Error ====== Annot Corrections ======== %d, %d, %d\n', acr(i,3), acr(i,4), annots(acr(i,3), 1));
            return;
        end
        annots(acr(i,3), 1) = annots(acr(i,3), 1) + acr(i,6);
    end
end