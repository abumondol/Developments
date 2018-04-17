clear; load('bpred_loso');
bres_loso = [];

for i = 1:15
    fprintf('LOSO RF: %d, \n', i);
    TP = 0;
    FP = 0;
    FN = 0;
    for subj = 1:4                        
        TPsub = 0;
        FPsub = 0;
        FNsub = 0;        
        for sess = 1:5               
            %fprintf('%d, %d, %d\n', subj, sess, i);
                       
            YTest = bpred_loso{i}{subj, sess}.YTest;
            YPred = bpred_loso{i}{subj, sess}.YPred;                                    
            
            [tp1, fp1, fn1] = find_tpfpfn(YTest, YPred);
            
            
            bres_loso(i).subject(subj).sessions(sess).TP = tp1;
            bres_loso(i).subject(subj).sessions(sess).FP = fp1;
            bres_loso(i).subject(subj).sessions(sess).FN = fn1;
            TPsub = TPsub + tp1;
            FPsub = FPsub + fp1;
            FNsub = FNsub + fn1;
        end       
        
        TP = TP + TPsub;
        FP = FP + FPsub;
        FN = FN + FNsub;
        
        bres_loso(i).subject(subj).TP = TPsub;
        bres_loso(i).subject(subj).FP = FPsub;
        bres_loso(i).subject(subj).FN = FNsub;            
        
        PR = TPsub/(TPsub+FPsub);
        RC = TPsub/(TPsub+FNsub);
        F1 = 2*PR*RC/(PR + RC);
        bres_loso(i).subject(subj).PR = PR;
        bres_loso(i).subject(subj).RC = RC;
        bres_loso(i).subject(subj).F1 = F1;            
       
    end
    
    bres_loso(i).TP = TP;
    bres_loso(i).FP = FP;
    bres_loso(i).FN = FN;            

    PR = TP/(TP+FP);
    RC = TP/(TP+FN);
    F1 = 2*PR*RC/(PR + RC);
    bres_loso(i).PR = PR;
    bres_loso(i).RC = RC;
    bres_loso(i).F1 = F1;                
end

save('bres_loso', 'bres_loso');