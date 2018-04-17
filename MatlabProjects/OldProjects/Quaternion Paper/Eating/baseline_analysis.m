s1 = baseline_res(1).scores;
s2 = baseline_res(2).scores;
s3 = baseline_res(3).scores;

res
a = sum(s2)
TP= a(1);
TN = a(2);
FP = a(3);
FN = a(4);

P = TP./(TP+FP)
R = TP./(TP+FN)
F1 = 2*(P.*R)./(P+R)
[P, R, F1]


