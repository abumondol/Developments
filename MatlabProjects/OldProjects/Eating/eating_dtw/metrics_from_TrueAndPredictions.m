function [Total, TP, TN, FP, FN, precision, recall, f1] = metrics_from_TrueAndPredictions(pred)
    TP = 0;
    TN = 0;
    FP = 0;
    FN = 0;    
    Total = length(pred);
    
    for i =1:Total
        if pred(i,1) > 400
            pred(i,1) = 0;
        end
        
        if pred(i,1)==0 && pred(i,2) == 0
            TN = TN + 1;
        elseif pred(i,1)==0 && pred(i,2) > 0
            FP = FP + 1;
        elseif pred(i,1)>0 && pred(i,2) == 0
            FN = FN + 1;
        elseif pred(i,1)>0 && pred(i,2) > 0
            TP = TP + 1;
        end
    end
    
    precision = TP/(TP+FP);
    recall = TP/(TP + FN);
    f1 = 2*precision*recall/(precision+recall);
end