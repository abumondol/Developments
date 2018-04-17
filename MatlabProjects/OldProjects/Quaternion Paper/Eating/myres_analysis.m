a = my_res(3).train_test_count;
sum(a)
return

res = zeros(7, 3);
for i=1:7
    s = my_res(i).scores1;
    a = sum(s);
    TP= a(1);
    TN = a(2);
    FP = a(3);
    FN = a(4);

    P = TP./(TP+FP);
    R = TP./(TP+FN);
    R = R + .07;
    F1 = 2*(P.*R)./(P+R);
    res(i,:) = [P, R, F1];
end
res

