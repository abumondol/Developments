
bres_lopo = [];

for i = 1:15
    fprintf('lopo RF: %d, \n', i);
    TP = 0;
    FP = 0;
    FN = 0;
    for subj = 1:4                                
        YTest = bpred_lopo{i}{subj}.YTest;        
        YPred = bpred_lopo{i}{subj}.YPred;                                            

        [TPsub, FPsub, FNsub] = find_tpfpfn(YTest, YPred);        

        TP = TP + TPsub;
        FP = FP + FPsub;
        FN = FN + FNsub;
        
        bres_lopo(i).subject(subj).TP = TPsub;
        bres_lopo(i).subject(subj).FP = FPsub;
        bres_lopo(i).subject(subj).FN = FNsub;            
        
        PR = TPsub/(TPsub+FPsub);
        RC = TPsub/(TPsub+FNsub);
        F1 = 2*PR*RC/(PR + RC);
        bres_lopo(i).subject(subj).PR = PR;
        bres_lopo(i).subject(subj).RC = RC;
        bres_lopo(i).subject(subj).F1 = F1;            
       
    end
    
    bres_lopo(i).TP = TP;
    bres_lopo(i).FP = FP;
    bres_lopo(i).FN = FN;            

    PR = TP/(TP+FP);
    RC = TP/(TP+FN);
    F1 = 2*PR*RC/(PR + RC);
    bres_lopo(i).PR = PR;
    bres_lopo(i).RC = RC;
    bres_lopo(i).F1 = F1;                
end

save('bres_lopo', 'bres_lopo');