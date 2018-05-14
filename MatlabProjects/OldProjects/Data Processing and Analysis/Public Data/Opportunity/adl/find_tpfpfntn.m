function  [TP, FP, FN, TN] = find_tpfpfntn(YTest, YPred)    
    TP = 0; 
    FP = 0;
    FN = 0;
    TN = 0;
    
    count = size(YPred, 1);
    for i = 1:count 
        if YTest(i,1) == 1 && YPred(i,1) == 1
            TP = TP + 1;
        elseif YTest(i,1) == 1 && YPred(i,1) == 0
            FN = FN + 1;            
        elseif YTest(i,1) == 0 && YPred(i,1) == 1
            FP = FP + 1;
        else
            TN = TN + 1;
        end            
    end    
    
end