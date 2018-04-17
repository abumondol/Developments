function [TP, TN, FP, FN]= get_tf_count(testY, resY)
    TP = sum(testY==1 & resY==1);
    TN = sum(testY==0 & resY==0);
    FP = sum(testY==0 & resY==1);
    FN = sum(testY==1 & resY==0);
end