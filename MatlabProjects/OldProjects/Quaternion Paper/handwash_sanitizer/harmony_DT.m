res = zeros(10, 4);
for s = 1:10
    
    fprintf('%d\n', s);        
    train = [];
    test_hw = [];
    test_eat = [];
    for i = 1:10
        if i == s
            test_hw = features(i).hw;
            test_eat = features(i).eat;            
        else            
            train = [features(i).hw; features(i).eat;];            
        end
    end

    B = fitctree(train(:, 2:end-1), train(:, end));
    res_hw = predict(B, test_hw(:, 2:end-1));
    res_eat = predict(B, test_eat(:, 2:end-1));
    
    testY = test_hw(:, end);
    [TP, TN, FP, FN]= get_tf_count(testY, res_hw);

    testY = test_eat(:, end);
    [TP1, TN1, FP1, FN1]= get_tf_count(testY, res_eat);
    res(s,1) = TP+TP1;
    res(s,2) = TN+TN1;
    res(s,3) = FP+FP1;
    res(s,4) = FN+FN1;

end
res = sum(res);
TP = res(1);
TN = res(2);
FP = res(3);
FN = res(4);

P = TP./(TP+FP);
R = TP./(TP+FN);
F1 = 2*(P.*R)./(P+R);
res = [P, R, F1]
