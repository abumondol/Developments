function [PR, RC, F1] = find_prf(TP, FP, FN)

    PR = TP/(TP+FP);
    RC = TP/(TP+FN);
    F1 = 2*PR*RC/(PR + RC);

end